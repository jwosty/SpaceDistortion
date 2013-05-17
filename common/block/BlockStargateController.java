package jw.spacedistortion.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class BlockStargateController extends SDBlock {
	// The coordinate at which the textures for this block starts
	private int blockIndexInTexture;
	private int textureTop = 2;
	
	public BlockStargateController(int id, int _blockIndexInTexture) {
		super(id, Material.rock);
		blockIndexInTexture = _blockIndexInTexture;
	}
	
	// Returns the position of the first neighboring block found that is a stargate ring
	// Coordinates in returns are not relative to the given coordinates
	public int[] getStargateBlocks(World world, int x, int y, int z) {
		int[] blockPosition = null;
		int[][] neighbors = {
				{-1, 0, 0}, {1, 0, 0},
				{0, -1, 0}, {0, 1, 0},
				{0, 0, -1}, {0, 0, 1}
		};
		search:
		for (int i = 0; i < neighbors.length; i++) {
			int[] neighbor = neighbors[i];
			int bx = neighbor[0] + x;
			int by = neighbor[1] + y;
			int bz = neighbor[2] + z;
			if (world.getBlockId(bx, by, bz) != 0) {
				blockPosition = new int[]{bx, by, bz};
				break search;				
			}
		}
		return blockPosition;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		int[] stargateBlockPos = this.getStargateBlocks(world, x, y, z);
		if (stargateBlockPos != null) {
			int bx = stargateBlockPos[0];
			int by = stargateBlockPos[1];
			int bz = stargateBlockPos[2];
			world.setBlock(bx, by, bz, SDBlock.eventHorizon.blockID);
		}
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
}
