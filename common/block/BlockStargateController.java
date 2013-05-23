package jw.spacedistortion.common.block;

import java.util.List;

import jw.spacedistortion.StringGrid;
import jw.spacedistortion.common.CommonProxy;
import jw.spacedistortion.common.tileentity.TileEntityStargateController;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

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
	
	@Override
	public TileEntity createNewTileEntity(World world) {
		System.out.println("Creating stargate controller tile entity...");
		return new TileEntityStargateController();
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
	public void onBlockAdded(World world, int x, int y, int z) {
		//boolean[][] stargateRing = this.getStargateBlocks(world, x, y, z);
		//if (stargateRing != null) {
			//world.setBlock(x, y, z, Block.blockLapis.blockID);
		//}
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
