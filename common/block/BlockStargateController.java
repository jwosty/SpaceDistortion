package jw.spacedistortion.common.block;

import java.util.List;

import jw.spacedistortion.StringGrid;
import jw.spacedistortion.common.CommonProxy;
import jw.spacedistortion.common.tileentity.TileEntityStargateController;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class BlockStargateController extends BlockContainer {
	public static StringGrid stargateRingShape = new StringGrid(
			"  XXX",
			" X   X",
			"X     X",
			"X     X",
			"X     X",
			" X   X",
			"  XXX");
	
	// The coordinate at which the textures for this block starts
	private int blockIndexInTexture;
	private int textureTop = 2;
	
	public BlockStargateController(int id, int _blockIndexInTexture) {
		super(id, Material.rock);
		blockIndexInTexture = _blockIndexInTexture;
	}
	
	// Returns the coordinates of the dominate (first found) stargate controller
	// in the given chunk; null if none is found
	public static int[] getDominantController(World world, int chunkX,
			int chunkZ) {
		System.out.println("Searching for Stargate at chunk (" + chunkX + ", " + chunkZ + ")");
		Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < 256; y++) {
					int block = chunk.getBlockID(x, y, z);
					int rx = (chunkX << 4) + x;
					int rz = (chunkZ << 4) + z;
					//System.out.println("Found Stargate");
					if (block == Block.blockGold.blockID) {
						System.out.println("Found gold at (" + rx + ", " + y + ", " + rz + ")");
						return new int[] { rx, y, rz };
					}
					//if (block == SDBlock.stargateController.blockID) {
					//	return new int[] { rx, y, rz };
					//}
				}
			}
		}
		return null;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		return new TileEntityStargateController();
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par1, float par2, float par3, float par4) {
		if (!world.isRemote) {
			System.out.println("Block was activated");
			TileEntityStargateController tileEntity = (TileEntityStargateController) world
					.getBlockTileEntity(x, y, z);
			if (tileEntity != null) {
				// world.setBlock(tileEntity.xDest, tileEntity.yDest,
				// tileEntity.zDest, Block.oreCoal.blockID);
				System.out.println("Teleporting to (" + tileEntity.xDest + ", "
						+ tileEntity.yDest + ", " + tileEntity.zDest + ")");
				player.setPosition(tileEntity.xDest, tileEntity.yDest, tileEntity.zDest);
			}
		}
		return true;
	}
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		TileEntityStargateController tileEntity = (TileEntityStargateController) world
				.getBlockTileEntity(x, y, z);
		if (tileEntity != null) {
			int[] targetCoords = BlockStargateController.getDominantController(
					world, x >> 4, z >> 4);
			if (targetCoords != null) {
				tileEntity.xDest = targetCoords[0];
				tileEntity.yDest = targetCoords[1];
				tileEntity.zDest = targetCoords[2];
				System.out.println("Found a stargate at (" + tileEntity.xDest + ", " + tileEntity.yDest + ", " + tileEntity.zDest + ")");
			} else {
				System.out
						.println("No stargate found; setting destination to ("
								+ x + ", " + y + ", " + z + ")");
				tileEntity.xDest = x;
				tileEntity.yDest = y;
				tileEntity.zDest = z;
			}
		}
	}
	
	// Returns the position of the first neighboring block found that is a stargate ring
	// Coordinates in returns are not relative to the given coordinates
	// Does nothing yet
	public boolean[][] getStargateBlocks(World world, int xOrigin, int yOrigin, int zOrigin) {
		// Hmm, a bit of an odd workaround... Make that method static? :/
		List<Integer[]> neighbors = ((SDBlock)SDBlock.stargateRing).getNeighboringBlocks(world, xOrigin, yOrigin, zOrigin);
		for (int i = 0; i < neighbors.size(); i++) {
			Integer[] blockInfo = neighbors.get(i);
			if (blockInfo[3] == SDBlock.stargateRing.blockID) {
				boolean[][] blocks = ((SDBlock) SDBlock.stargateRing).detectStructure(world, stargateRingShape, blockInfo[0], blockInfo[1], blockInfo[2]);
				if (blocks != null) {
					return blocks;
				}
			}
		}
		return null;
	}
	
	
	@Override
	public int getBlockTextureFromSide(int side) {
		int offset;
		if (side == 1) {
			offset = 1;
		} else {
			offset = 0;
		}
		return blockIndexInTexture + offset;
	}
	
	@Override
	public String getTextureFile() {
		return CommonProxy.TEXTURES_PNG;
	}
}
