package jw.spacedistortion.common.block;

import java.util.ArrayList;
import java.util.List;

import jw.spacedistortion.Axis;
import jw.spacedistortion.Pair;
import jw.spacedistortion.StringGrid;
import jw.spacedistortion.Triplet;
import jw.spacedistortion.client.gui.GuiDHD;
import jw.spacedistortion.common.network.ChannelHandler;
import jw.spacedistortion.common.network.packet.IPacket;
import jw.spacedistortion.common.network.packet.PacketWormhole;
import jw.spacedistortion.common.tileentity.StargateControllerState;
import jw.spacedistortion.common.tileentity.TileEntityEventHorizon;
import jw.spacedistortion.common.tileentity.TileEntityStargateController;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
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
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockStargateController extends SDBlock implements ITileEntityProvider {
	public static StringGrid stargateRingShape = new StringGrid(
			"  XXX  ",
			" X   X ",
			"X     X",
			"X     X",
			"X     X",
			" X   X ",
			"  XXX  ");
	public static StringGrid stargateEventHorizonShape = new StringGrid(
			"       ",
			"  XXX  ",
			" XXXXX ",
			" XXXXX ",
			" XXXXX ",
			"  XXX  ",
			"       ");

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
			return StargateControllerState.READY;
		} else {
			return StargateControllerState.NO_CONNECTED_STARGATE;
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
	
	@Override
	/** Called when the block is right-clicked on **/
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int par1, float par2, float par3, float par4) {
		if (!world.isRemote) {
			TileEntityStargateController tileEntity = (TileEntityStargateController) world.getTileEntity(x, y, z);
			Minecraft.getMinecraft().displayGuiScreen(new GuiDHD(tileEntity));
		}
		return true;
	}
	
	@Override
	public void onNeighborChange(IBlockAccess world, int x, int y, int z, int otherX, int otherY, int otherZ) {
		StargateControllerState state = BlockStargateController.getCurrentState(world, x, y, z);
		TileEntityStargateController controllerTileEntity = (TileEntityStargateController) world.getTileEntity(x, y, z);
		if (controllerTileEntity != null) {
			//controllerTileEntity.lastState = state;
			SDBlock.syncTileEntity(controllerTileEntity);
			//world.markBlockForUpdate(x, y, z);
		}
	}
	
	public Triplet<Integer, Integer, Integer> decodeAddress(byte[] address) {
		// Building base 39 numbers using powers of 3
		int chunkX = (int) ((address[0] * 1521) + (address[1] * 39) + address[2]);
		int chunkZ = (int) ((address[3] * 1521) + (address[4] * 39) + address[5]);
		int last = (int) address[6];
		// The dimension is stored in the last 2 bits of the last number/symbol (the mask is 0b11)
		int dimension = last & 3;
		// The sign of the x coordinate is the 4rd to last bit (the mask is 0b1000)
		int xSign = last & 8;
		// The sign of the z coordinate is the 3th to last bit (the mask is 0b100)
		int zSign = last & 4;
		if (xSign == 0) {
			chunkX = -chunkX;
		}
		if (zSign == 0) {
			chunkZ = -chunkZ;
		}
		return new Triplet(dimension, chunkX, chunkZ);
	}

	@SideOnly(Side.CLIENT)
	public void connectionCreate(TileEntityStargateController tileEntity) {
		if (Minecraft.getMinecraft().theWorld.isRemote) {
			ChannelHandler.clientSendPacket(new PacketWormhole(
				tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, 
				tileEntity.dialingAddress, true));
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void connectionSever(TileEntityStargateController tileEntity) {
		if (Minecraft.getMinecraft().theWorld.isRemote) {
			ChannelHandler.clientSendPacket(new PacketWormhole(
				tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord,
				tileEntity.dialingAddress, false));
		}
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
		Pair<Axis, ArrayList<Triplet<Integer, Integer, Integer>>> dstPlaneBlocks = this.getStargateCenterBlocks(world, dstX, dstY, dstZ);
		Pair<Axis, ArrayList<Triplet<Integer, Integer, Integer>>> srcPlaneBlocks = this.getStargateCenterBlocks(world, srcX, srcY, srcZ);
		if (srcPlaneBlocks == null || dstPlaneBlocks == null) {
			// stop; one of the stargate controllers isn't connected to a stargate
			return;
		}
		Axis dstAxis = dstPlaneBlocks.X;
		ArrayList<Triplet<Integer, Integer, Integer>> dstBlocks = dstPlaneBlocks.Y;
		Axis srcAxis = srcPlaneBlocks.X;
		ArrayList<Triplet<Integer, Integer, Integer>> srcBlocks = srcPlaneBlocks.Y;
		
		// Fill the dialing stargate
		for (int i = 0; i < srcBlocks.size(); i++) {
			basicUnsafeFillStargateCenter(world, dstAxis, dstBlocks, srcAxis, srcBlocks, i, SDBlock.eventHorizon);
		}
		
		// Set the states of both stargates
		TileEntityStargateController srcTileEntity = (TileEntityStargateController) world.getTileEntity(srcX, srcY, srcZ);
		if (srcTileEntity != null) {
			srcTileEntity.state = StargateControllerState.ACTIVE_OUTGOING;
			srcTileEntity.resetAddress();
			world.markBlockForUpdate(srcX, srcY, srcZ);
		}
		TileEntityStargateController dstTileEntity = (TileEntityStargateController) world.getTileEntity(dstX, dstY, dstZ);
		if (dstTileEntity != null) {
			dstTileEntity.state = StargateControllerState.ACTIVE_INCOMING;
			dstTileEntity.resetAddress();
			world.markBlockForUpdate(dstX, dstY, dstZ);
		}
	}

	/**
	 * Deactivate the stargates attached to the given controllers
	 */
	public void serverDeactivateStargatePair(World world, int srcX, int srcY, int srcZ,
			int dstX, int dstY, int dstZ) {
		Pair<Axis, ArrayList<Triplet<Integer, Integer, Integer>>> dstPlaneBlocks = this.getStargateCenterBlocks(world, dstX, dstY, dstZ);
		Pair<Axis, ArrayList<Triplet<Integer, Integer, Integer>>> srcPlaneBlocks = this.getStargateCenterBlocks(world, srcX, srcY, srcZ);
		if (srcPlaneBlocks == null || dstPlaneBlocks == null) {
			// stop; one of the stargate controllers isn't connected to a stargate
			return;
		}
		Axis dstAxis = dstPlaneBlocks.X;
		ArrayList<Triplet<Integer, Integer, Integer>> dstBlocks = dstPlaneBlocks.Y;
		Axis srcAxis = srcPlaneBlocks.X;
		ArrayList<Triplet<Integer, Integer, Integer>> srcBlocks = srcPlaneBlocks.Y;
		
		// Fill the dialing stargate
		for (int i = 0; i < srcBlocks.size(); i++) {
			basicUnsafeFillStargateCenter(world, dstAxis, dstBlocks, srcAxis, srcBlocks, i, Blocks.air);
		}

		TileEntityStargateController srcTileEntity = (TileEntityStargateController) world.getTileEntity(srcX, srcY, srcZ);
		if (srcTileEntity != null) {
			srcTileEntity.state = StargateControllerState.READY;
			world.markBlockForUpdate(srcX, srcY, srcZ);
		}
		
		TileEntityStargateController dstTileEntity = (TileEntityStargateController) world.getTileEntity(srcX, srcY, srcZ);
		if (dstTileEntity != null) {
			dstTileEntity.state = StargateControllerState.READY;
			world.markBlockForUpdate(dstX, dstY, dstZ);
		}
	}
	
	private void basicUnsafeFillStargateCenter(World world, Axis dstAxis,
			ArrayList<Triplet<Integer, Integer, Integer>> dstBlocks, Axis srcAxis,
			ArrayList<Triplet<Integer, Integer, Integer>> srcBlocks, int i,
			Block fillMaterial) {
		Triplet<Integer, Integer, Integer> srcBlockCoords = srcBlocks.get(i);
		Triplet<Integer, Integer, Integer> dstBlockCoords = dstBlocks.get(i);
		// Set the destination block
		world.setBlock(srcBlockCoords.X, srcBlockCoords.Y, srcBlockCoords.Z, fillMaterial);
		// Fill the target stargate with the block
		world.setBlock(dstBlockCoords.X, dstBlockCoords.Y, dstBlockCoords.Z, fillMaterial);
		
		// When the fill block is event horizon, set the tile entity info
		if (fillMaterial == SDBlock.eventHorizon) {
			// Set the tile entity that stores the specific destination coordinates
			TileEntityEventHorizon srcTileEntity = (TileEntityEventHorizon) world.getTileEntity(srcBlockCoords.X, srcBlockCoords.Y,srcBlockCoords.Z);
			if (srcTileEntity != null) {
				srcTileEntity.isOutgoing = true;
				srcTileEntity.axis = srcAxis;
				srcTileEntity.destX = dstBlockCoords.X;
				srcTileEntity.destY = dstBlockCoords.Y;
				srcTileEntity.destZ = dstBlockCoords.Z;
			}
			
			TileEntityEventHorizon dstTileEntity = (TileEntityEventHorizon) world.getTileEntity(dstBlockCoords.X, dstBlockCoords.Y, dstBlockCoords.Z);
			// Blank out the target's tile entity data if the fill is eventHorizon
			if (dstTileEntity != null) {
				dstTileEntity.isOutgoing = false;
				dstTileEntity.axis = dstAxis;
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
	public Pair<Axis, ArrayList<Triplet<Integer, Integer, Integer>>> getStargateCenterBlocks(World world, int x, int y, int z) {
		ArrayList<Triplet<Integer, Integer, Integer>> results = new ArrayList();
		// See if there's really a stargate here
		DetectStructureResults stargate = this.getStargateBlocks(world, x, y, z);
		if (stargate == null) {
			return null;
		}
		Triplet<Integer, Integer, Integer> relativeOrigin = this.templateToWorldCoordinates(-stargate.xOffset, stargate.yOffset, stargate.axis);
		Triplet<Integer, Integer, Integer> origin = new Triplet<Integer, Integer, Integer>(
				stargate.firstNeighbor.X + relativeOrigin.X,
				stargate.firstNeighbor.Y + relativeOrigin.Y,
				stargate.firstNeighbor.Z + relativeOrigin.Z);
		for (int templateX = 0; templateX <= stargateEventHorizonShape.width; templateX++) {
			for (int templateY = 0; templateY <= stargateEventHorizonShape.height; templateY++) {
				if (stargateEventHorizonShape.get(templateX, templateY) == 'X') {
					Triplet<Integer, Integer, Integer> coords = this
							.getBlockInStructure(world, origin.X, origin.Y,
									origin.Z, templateX, -templateY,
									stargate.axis);
					results.add(new Triplet(coords.X, coords.Y, coords.Z));
				}
			}
		}
		return new Pair(stargate.axis, results);
	}

	/**
	 * Returns the position of the first neighboring block found that is a
	 * stargate ring. Coordinates in returns are not relative to the given
	 * coordinates
	 **/
	public DetectStructureResults getStargateBlocks(IBlockAccess world, int xOrigin,
			int yOrigin, int zOrigin) {
		List<Pair<Integer[], Block>> neighbors = this.getNeighboringBlocks(world, xOrigin, yOrigin, zOrigin);
		for (int i = 0; i < neighbors.size(); i++) {
			Pair<Integer[], Block> blockInfo = neighbors.get(i);
			Integer[] coords = blockInfo.X;
			Block type = blockInfo.Y;
			if (type == SDBlock.stargateRing) {
				DetectStructureResults results = SDBlock.detectStructure(world,
						stargateRingShape, coords[0], coords[1],
						coords[2], SDBlock.stargateRing);
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
		TileEntityStargateController tileEntity = (TileEntityStargateController) world.getTileEntity(x, y, z);
		if (side == facing) {
			switch (tileEntity.state) {
			case NO_CONNECTED_STARGATE:
				return this.controllerOff;
			case READY:
				return this.controllerIdle;
			case ACTIVE_OUTGOING:
				return this.controllerActive;
			case ACTIVE_INCOMING:
				return this.controllerActive;
			}
		}
		return this.blockIcon;
	}
}
