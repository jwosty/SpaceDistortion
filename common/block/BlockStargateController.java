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
	public int[] getRelativeStargateBlocks(World world, int x, int y, int z) {
		int[] blockPosition = null;
		int[][] offsets = {
				{-1, 0, 0}, {1, 0, 0},
				{0, -1, 0}, {0, 1, 0},
				{0, 0, -1}, {0, 0, 1}
		};
		search:
		for (int i = 0; i < offsets.length; i++) {
			int[] offset = offsets[i];
			int bx = offset[0];
			int by = offset[1];
			int bz = offset[2];
			if (world.getBlockId(bx, by, bz) != 0) {
				blockPosition = new int[]{bx, by, bz};
				break search;				
			}
		}
		return blockPosition;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		int[] stargateBlockPos = this.getRelativeStargateBlocks(world, x, y, z);
		if (stargateBlockPos != null) {
			world.setBlock(stargateBlockPos[0], stargateBlockPos[1], stargateBlockPos[2], SDBlock.eventHorizon.blockID);
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
