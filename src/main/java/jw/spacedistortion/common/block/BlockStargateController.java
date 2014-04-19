package jw.spacedistortion.common.block;

import java.util.ArrayList;

import jw.spacedistortion.Triplet;
import jw.spacedistortion.common.SpaceDistortion;
import jw.spacedistortion.common.tileentity.StargateControllerState;
import jw.spacedistortion.common.tileentity.StargateControllerState.StargateControllerActive;
import jw.spacedistortion.common.tileentity.StargateControllerState.StargateControllerInvalid;
import jw.spacedistortion.common.tileentity.StargateControllerState.StargateControllerReady;
import jw.spacedistortion.common.tileentity.TileEntityStargateController;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
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
		Structure stargate = Structure.detectConnectedStructure(world, x, y, z, SpaceDistortion.stargateRingShape, SpaceDistortion.stargateRingShapeInfo);
		if (stargate == null) {
			return new StargateControllerState.StargateControllerInvalid();
		} else {
			if (world instanceof World & !((World)world).isRemote) {
				System.out.println("Found stargate facing " + stargate.facing + " at (" + stargate.x + ", " + stargate.y + ", " + stargate.z + ")");
			}
			return new StargateControllerState.StargateControllerReady(new byte[] { 40, 40, 40, 40, 40, 40, 40 }, 0);
		}
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemStack) {
		// no need to figure out the right orientation again when the piston block can do it for us
		int direction = BlockPistonBase.determineOrientation(world, x, y, z, entity);
		world.setBlockMetadataWithNotify(x, y, z, direction, 2);
		
		TileEntityStargateController controllerTileEntity = (TileEntityStargateController) world.getTileEntity(x, y, z);
		controllerTileEntity.state = BlockStargateController.getCurrentState(world, x, y, z);
		
		//Structure.detectStructure(world, x, y, z, SpaceDistortion.stargateRingShape, SpaceDistortion.stargateRingShapeInfo);
	}
	
	private void explode(World world, EntityLivingBase explosionCausingJerk, int x, int y, int z) {
		TileEntityStargateController tileEntity = (TileEntityStargateController)world.getTileEntity(x, y, z);
		if (tileEntity.state instanceof StargateControllerActive) {
			StargateControllerActive state = (StargateControllerActive) tileEntity.state;
			if (state.isOutgoing) {
			//this.serverDeactivateStargatePair(
			//		world, x, y, z,
			//		state.connectedXCoord, state.connectedYCoord, state.connectedZCoord);
			} else {
			//this.serverDeactivateStargatePair(
			//		world, state.connectedXCoord, state.connectedYCoord, state.connectedZCoord,
			//		x, y, z);
			}
		}
		//world.createExplosion(explosionCausingJerk, x, y, z, 3.5f, true);
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
