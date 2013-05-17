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
	// Does nothing yet
	public int[] getStargateBlocks(World world, int x, int y, int z) {
		return new int[]{};
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		int[] stargateBlockPos = this.getNeighboringBlocks(world, x, y, z);
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
