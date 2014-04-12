package jw.spacedistortion.common.block;

import java.util.ArrayList;

import jw.spacedistortion.Pair;
import jw.spacedistortion.Triplet;
import jw.spacedistortion.client.SDSoundHandler;
import jw.spacedistortion.common.SpaceDistortion;
import jw.spacedistortion.common.tileentity.StargateControllerState;
import jw.spacedistortion.common.tileentity.StargateControllerState.StargateControllerActive;
import jw.spacedistortion.common.tileentity.StargateControllerState.StargateControllerInvalid;
import jw.spacedistortion.common.tileentity.StargateControllerState.StargateControllerReady;
import jw.spacedistortion.common.tileentity.TileEntityEventHorizon;
import jw.spacedistortion.common.tileentity.TileEntityStargateController;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockStargateController extends SDBlock implements ITileEntityProvider {
	@SideOnly(Side.CLIENT)
	private IIcon controllerOff;
	@SideOnly(Side.CLIENT)
	private IIcon controllerIdle;
	@SideOnly(Side.CLIENT)
	private IIcon controllerActive;

	@Override
	public boolean hasTileEntity(int metadata) {
		return true;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityStargateController();
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		super.registerBlockIcons(register);
		this.controllerOff = register.registerIcon(this.getIconName() + "_off");
		this.controllerIdle = register.registerIcon(this.getIconName() + "_idle");
		this.controllerActive = register.registerIcon(this.getIconName() + "_active");
	}

	public BlockStargateController() {
		super(Material.rock);
	}
	
	/**
	 * Finds the dominant stargate in a chunk
	 * @param world the world to search in
	 * @param chunkX the chunk's x coordinate
	 * @param chunkZ the chunk's y coordinate
	 * @return The coordinates of the dominant stargate controller (this is simply the first one found)
	 */
	public static int[] getDominantController(World world, int chunkX,
			int chunkZ) {
		Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < 256; y++) {
					Block block = chunk.getBlock(x, y, z);
					int rx = (chunkX << 4) + x;
					int rz = (chunkZ << 4) + z;
					if (block == SDBlock.stargateController) {
						return new int[] { rx, y, rz };
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * Retrieves the current state of the stargate associated with this stargate controller
	 * @param world the world
	 * @param x the x position of the controller
	 * @param y the y position of the controller
	 * @param z the z position of the controller
	 * @return A StargateControllerState the describes the stargate's state
	 */
	public static StargateControllerState getCurrentState(IBlockAccess world, int x, int y, int z) {
		if (SDBlock.stargateController.getStargateBlocks(world, x, y, z) != null) {
			return new StargateControllerState.StargateControllerReady(new byte[] { 40, 40, 40, 40, 40, 40, 40 }, 0);
		} else {
			return new StargateControllerState.StargateControllerInvalid();
		}
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemStack) {
		// no need to figure out the right orientation again when the piston block can do it for us
		int direction = BlockPistonBase.determineOrientation(world, x, y, z, entity);
		world.setBlockMetadataWithNotify(x, y, z, direction, 2);
		
		TileEntityStargateController controllerTileEntity = (TileEntityStargateController) world.getTileEntity(x, y, z);
		controllerTileEntity.state = BlockStargateController.getCurrentState(world, x, y, z);
	}
	
	private void explode(World world, EntityLivingBase explosionCausingJerk, int x, int y, int z) {
		TileEntityStargateController tileEntity = (TileEntityStargateController)world.getTileEntity(x, y, z);
		if (tileEntity.state instanceof StargateControllerActive) {
			StargateControllerActive state = (StargateControllerActive) tileEntity.state;
			if (state.isOutgoing) {
			this.serverDeactivateStargatePair(
					world, x, y, z,
					state.connectedXCoord, state.connectedYCoord, state.connectedZCoord);
			} else {
			this.serverDeactivateStargatePair(
					world, state.connectedXCoord, state.connectedYCoord, state.connectedZCoord,
					x, y, z);
			}
		}
		world.createExplosion(explosionCausingJerk, x, y, z, 3.5f, true);
	}
	
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int metadata) {
		TileEntityStargateController tileEntity = (TileEntityStargateController)world.getTileEntity(x, y, z);
		if (!world.isRemote && tileEntity.state instanceof StargateControllerActive) {
			this.explode(world, null, x, y, z);
		}
		super.breakBlock(world, x, y, z, block, metadata);
	}
	
	@Override
	/** Called when the block is right-clicked on **/
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int par1, float par2, float par3, float par4) {
		if (world.isRemote) {
			TileEntityStargateController tileEntity = (TileEntityStargateController) world.getTileEntity(x, y, z);
			player.openGui(SpaceDistortion.instance, 0, world, x, y, z);
		}
		return true;
	}
	
	@Override
	public void onNeighborChange(IBlockAccess world, int x, int y, int z, int otherX, int otherY, int otherZ) {
		StargateControllerState state = BlockStargateController.getCurrentState(world, x, y, z);
		TileEntityStargateController controllerTileEntity = (TileEntityStargateController) world.getTileEntity(x, y, z);
		if (controllerTileEntity != null) {
			SDBlock.syncTileEntity(controllerTileEntity);
		}
	}
	
	public Triplet<Integer, Integer, Integer> decodeAddress(byte[] address) {
		// Building base 39 numbers using powers of 3
		int chunkX = (int) ((address[0] * 1521) + (address[1] * 39) + address[2]);
		int chunkZ = (int) ((address[3] * 1521) + (address[4] * 39) + address[5]);
		int last = address[6];
		// dimension is last 2 bits (0b11)
		int dimension = last & 3;
		// x coordinate sign is 4th to last bit (0b1000)
		if ((last & 8) == 0) {
			chunkX = -chunkX;
		}
		// z coordinate sign is 3rd to last bit (the mask is 0b100)
		if ((last & 4) == 0) {
			chunkZ = -chunkZ;
		}
		return new Triplet(dimension, chunkX, chunkZ);
	}
	
	private ArrayList<Integer> convertBase(int n, int base) {
		ArrayList<Integer> ar = new ArrayList();
		while (n > 0) {
			ar.add(0, n % base);
			n = n / base;
		}
		return ar;
	}
	
	public byte[] encodeAddress(int chunkX, int chunkZ, int dimension) {
		ArrayList<Integer> cx = this.convertBase(Math.abs(chunkX), 39);
		ArrayList<Integer> cz = this.convertBase(Math.abs(chunkZ), 39);
		ArrayList<Integer> dimAr = this.convertBase(dimension, 39);
		while (cx.size() < 3) {
			cx.add(0, 0);
		}
		while (cz.size() < 3) {
			cz.add(0, 0);
		}
		while(dimAr.size() < 3) {
			dimAr.add(0);
		}
		// Dimension is last two bits (0b11)
		int d = (byte)(dimAr.get(0) & 3);
		// x coordinate sign is 4th to last bit (0b1000)
		if (chunkX >= 0) { d = d | 8; }
		//if (chunkX < 0) { d += 8; };
		// z coordinate sign is 3rd to last bit (0b100)
		if (chunkZ >= 0) { d = d | 4; }
		// Build result
		return new byte[] {
				(byte)(int)cx.get(0), (byte)(int)cx.get(1), (byte)(int)cx.get(2),
				(byte)(int)cz.get(0), (byte)(int)cz.get(1), (byte)(int)cz.get(2),
				(byte)d
		};
	}
	
	/**
	 * Activate the stargates attached to the given controllers
	 */
	public void serverActivateStargatePair(World world, int srcX, int srcY, int srcZ,
			int dstX, int dstY, int dstZ) {
		if (world.getBlock(srcX, srcY, srcZ) != SDBlock.stargateController || world.getBlock(dstX, dstY, dstZ) != SDBlock.stargateController) {
			// stop; there's no stargate controller
			return;
		}
		// Get the source and target stargate center coordinates
		Pair<ForgeDirection, ArrayList<Triplet<Integer, Integer, Integer>>> dstPlaneBlocks = this.getStargateCenterBlocks(world, dstX, dstY, dstZ);
		Pair<ForgeDirection, ArrayList<Triplet<Integer, Integer, Integer>>> srcPlaneBlocks = this.getStargateCenterBlocks(world, srcX, srcY, srcZ);
		TileEntityStargateController srcTileEntity = null;
		TileEntityStargateController dstTileEntity = null;
		if (srcPlaneBlocks == null) {
			return;
		} else {
			srcTileEntity = (TileEntityStargateController) world.getTileEntity(srcX, srcY, srcZ);
			if (dstPlaneBlocks == null) {
				srcTileEntity.reset();
				return;
			} else {
				dstTileEntity = (TileEntityStargateController) world.getTileEntity(dstX, dstY, dstZ);
			}
		}
		ForgeDirection dstFacing = dstPlaneBlocks.X;
		ArrayList<Triplet<Integer, Integer, Integer>> dstBlocks = dstPlaneBlocks.Y;
		ForgeDirection srcFacing = srcPlaneBlocks.X;
		ArrayList<Triplet<Integer, Integer, Integer>> srcBlocks = srcPlaneBlocks.Y;
		
		// Fill the dialing stargate
		for (int i = 0; i < srcBlocks.size(); i++) {
			basicUnsafeFillStargateCenter(world, dstFacing, dstBlocks, srcFacing, srcBlocks, i, SDBlock.eventHorizon);
		}
		
		// Set the appropriate states of both stargates
		srcTileEntity.state = new StargateControllerActive(
				true, dstTileEntity.xCoord, dstTileEntity.yCoord, dstTileEntity.zCoord,
				dstTileEntity.getWorldObj().provider.dimensionId);
		dstTileEntity.state = new StargateControllerActive(
				false, srcTileEntity.xCoord, srcTileEntity.yCoord, srcTileEntity.zCoord,
				srcTileEntity.getWorldObj().provider.dimensionId);
		
		world.markBlockForUpdate(srcX, srcY, srcZ);
		world.markBlockForUpdate(dstX, dstY, dstZ);
		
		// Play kawoosh sounds at both stargates
		SDSoundHandler.serverPlaySoundToPlayers(
				world.playerEntities, "stargate.kawoosh", 1F, 1F,
				(double) srcTileEntity.xCoord, (double) srcTileEntity.yCoord, (double) srcTileEntity.zCoord);
		
		SDSoundHandler.serverPlaySoundToPlayers(
				world.playerEntities, "stargate.kawoosh", 1F, 1F,
				(double) dstTileEntity.xCoord, (double) dstTileEntity.yCoord, (double) dstTileEntity.zCoord);
	}

	/**
	 * Deactivate the stargates attached to the given controllers
	 */
	public void serverDeactivateStargatePair(World world, int srcX, int srcY, int srcZ,
			int dstX, int dstY, int dstZ) {
		Pair<ForgeDirection, ArrayList<Triplet<Integer, Integer, Integer>>> dstPlaneBlocks = this.getStargateCenterBlocks(world, dstX, dstY, dstZ);
		Pair<ForgeDirection, ArrayList<Triplet<Integer, Integer, Integer>>> srcPlaneBlocks = this.getStargateCenterBlocks(world, srcX, srcY, srcZ);
		
		ForgeDirection srcFacing;
		ArrayList<Triplet<Integer, Integer, Integer>> srcBlocks;
		ForgeDirection dstFacing;
		ArrayList<Triplet<Integer, Integer, Integer>> dstBlocks;
		
		if (srcPlaneBlocks == null) {
			srcFacing = null;
			srcBlocks = null;
		} else {
			srcBlocks = srcPlaneBlocks.Y;
			srcFacing = srcPlaneBlocks.X;
		}
		if (dstPlaneBlocks == null) {
			dstFacing = null;
			dstBlocks = null;
		} else {
			dstFacing = dstPlaneBlocks.X;
			dstBlocks = dstPlaneBlocks.Y;			
		}
		
		// Fill the dialing stargate
		for (int i = 0; i < srcBlocks.size(); i++) {
			basicUnsafeFillStargateCenter(world, dstFacing, dstBlocks, srcFacing, srcBlocks, i, Blocks.air);
		}

		if (srcPlaneBlocks != null) {
			TileEntityStargateController srcTileEntity = (TileEntityStargateController) world.getTileEntity(srcX, srcY, srcZ);
			srcTileEntity.reset();
			world.markBlockForUpdate(srcX, srcY, srcZ);
			
			// Play deactivation sound at outgoing stargate
			SDSoundHandler.serverPlaySoundToPlayers(
					world.playerEntities, "stargate.close", 1F, 1F,
					(double) srcTileEntity.xCoord, (double) srcTileEntity.yCoord, (double) srcTileEntity.zCoord);
		}
		
		if (dstPlaneBlocks != null) {
			TileEntityStargateController dstTileEntity = (TileEntityStargateController) world.getTileEntity(dstX, dstY, dstZ);
			dstTileEntity.reset();
			world.markBlockForUpdate(dstX, dstY, dstZ);
			
			// Play deactivation sound at incoming stargate
			SDSoundHandler.serverPlaySoundToPlayers(
					world.playerEntities, "stargate.close", 1F, 1F,
					(double) dstTileEntity.xCoord, (double) dstTileEntity.yCoord, (double) dstTileEntity.zCoord);
		}
	}
	
	private void basicUnsafeFillStargateCenter(World world, ForgeDirection dstFacing,
			ArrayList<Triplet<Integer, Integer, Integer>> dstBlocks, ForgeDirection srcFacing,
			ArrayList<Triplet<Integer, Integer, Integer>> srcBlocks, int i,
			Block fillMaterial) {
		Triplet<Integer, Integer, Integer> srcBlockCoords;
		Triplet<Integer, Integer, Integer> dstBlockCoords;
		if (srcBlocks == null) {
			srcBlockCoords = null;
		} else {
			srcBlockCoords = srcBlocks.get(i);
			world.setBlock(srcBlockCoords.X, srcBlockCoords.Y, srcBlockCoords.Z, fillMaterial);
		}
		if (dstBlocks == null) {
			dstBlockCoords = null;
		} else {
			dstBlockCoords = dstBlocks.get(i);
			world.setBlock(dstBlockCoords.X, dstBlockCoords.Y, dstBlockCoords.Z, fillMaterial);			
		}
		
		// When the fill block is event horizon, set the tile entity info
		if (fillMaterial == SDBlock.eventHorizon) {
			// Set the tile entity that stores the specific destination coordinates
			if (srcBlockCoords != null) {
				TileEntityEventHorizon srcTileEntity = (TileEntityEventHorizon) world.getTileEntity(srcBlockCoords.X, srcBlockCoords.Y,srcBlockCoords.Z);
				srcTileEntity.isOutgoing = true;
				srcTileEntity.facing = srcFacing;
				srcTileEntity.destX = dstBlockCoords.X;
				srcTileEntity.destY = dstBlockCoords.Y;
				srcTileEntity.destZ = dstBlockCoords.Z;
				if (i == srcBlocks.size() / 2 ) {
					// Event horizon blocks with metadata of 1 play a loop sound
					world.setBlockMetadataWithNotify(srcTileEntity.xCoord, srcTileEntity.yCoord, srcTileEntity.zCoord, 1, 3);
				}
			}
			
			if (dstBlockCoords != null) {
				TileEntityEventHorizon dstTileEntity = (TileEntityEventHorizon) world.getTileEntity(dstBlockCoords.X, dstBlockCoords.Y, dstBlockCoords.Z);
				// Blank out the target's tile entity data
				dstTileEntity.isOutgoing = false;
				dstTileEntity.facing = dstFacing;
				if (i == srcBlocks.size() / 2) {
					// Event horizon blocks with metadata of 1 play a loop sound
					world.setBlockMetadataWithNotify(dstTileEntity.xCoord, dstTileEntity.yCoord, dstTileEntity.zCoord, 1, 3);
				}
			}
		}
	}

	/**
	 * Returns the coordinates of all the center blocks at the given stargate,
	 * or null if there's no stargate there.
	 * 
	 * @param world
	 *            The world that the stargate is located in
	 * @param x
	 *            The x coordinate of the stargate controller
	 * @param y
	 *            The y coordinate of the stargate controller
	 * @param z
	 *            The z coordinate of the stargate controller
	 * @return
	 */
	public Pair<ForgeDirection, ArrayList<Triplet<Integer, Integer, Integer>>> getStargateCenterBlocks(World world, int x, int y, int z) {
		ArrayList<Triplet<Integer, Integer, Integer>> results = new ArrayList();
		// See if there's really a stargate here
		DetectStructureResults stargate = this.getStargateBlocks(world, x, y, z);
		if (stargate == null) {
			return null;
		}
		Triplet<Integer, Integer, Integer> relativeOrigin = this.templateToWorldCoordinates(-stargate.xOffset, stargate.yOffset, stargate.facing);
		Triplet<Integer, Integer, Integer> origin = new Triplet<Integer, Integer, Integer>(
				stargate.firstNeighbor.X + relativeOrigin.X,
				stargate.firstNeighbor.Y + relativeOrigin.Y,
				stargate.firstNeighbor.Z + relativeOrigin.Z);
		for (int templateX = 0; templateX <= SpaceDistortion.stargateEventHorizonShape.width; templateX++) {
			for (int templateY = 0; templateY <= SpaceDistortion.stargateEventHorizonShape.height; templateY++) {
				if (SpaceDistortion.stargateEventHorizonShape.get(templateX, templateY) == 'E') {
					Triplet<Integer, Integer, Integer> coords = this
							.getBlockInStructure(world, origin.X, origin.Y,
									origin.Z, templateX, -templateY,
									stargate.facing);
					results.add(new Triplet(coords.X, coords.Y, coords.Z));
				}
			}
		}
		return new Pair(stargate.facing, results);
	}

	/**
	 * Returns the position of the first neighboring block found that is a
	 * stargate ring. Coordinates in returns are not relative to the given
	 * coordinates
	 **/
	public DetectStructureResults getStargateBlocks(IBlockAccess world, int x,
			int y, int z) {
		for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
			int bx = x + direction.offsetX;
			int by = y + direction.offsetY;
			int bz = z + direction.offsetZ;
			Block block = world.getBlock(bx, by, bz);
			if (block == SDBlock.stargateRing || block == SDBlock.stargateRingChevron) {
				DetectStructureResults results = SDBlock.detectStructure(
						world, SpaceDistortion.stargateRingShape, bx, by, bz,
						SpaceDistortion.stargateRingShapeInfo);
				if (results != null) {
					return results;
				}
			}
		}
		return null;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
		int facing = world.getBlockMetadata(x, y, z);
		if (side == facing) {
			TileEntityStargateController tileEntity = (TileEntityStargateController) world.getTileEntity(x, y, z);
			if (tileEntity.state instanceof StargateControllerInvalid) {
				return this.controllerOff;
			} else if (tileEntity.state instanceof StargateControllerReady) {
				return this.controllerIdle;
			} else if (tileEntity.state instanceof StargateControllerActive) {
				return this.controllerActive;
			}
		}
		return this.blockIcon;
	}
}
