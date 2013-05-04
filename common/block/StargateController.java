package jw.spacedistortion.common.block;

import jw.spacedistortion.common.CommonProxy;
import net.minecraft.block.material.Material;

public class StargateController extends SDBlock {
	// The coordinate at which the textures for this block starts
	private int blockIndexInTexture;
	private int textureTop = 2;
	
	public StargateController(int id, int _blockIndexInTexture, Material material) {
		super(id, material);
		blockIndexInTexture = _blockIndexInTexture;
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
