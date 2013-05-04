package jw.spacedistortion.common.block;

import net.minecraft.block.material.Material;

public class BlockEventHorizon extends SDBlock {
	// The coordinate at which the textures for this block starts
	private int blockIndexInTexture;
	
	public BlockEventHorizon(int id, int _blockIndexInTexture, Material material) {
		super(id, material);
		blockIndexInTexture = _blockIndexInTexture;
	}
}
