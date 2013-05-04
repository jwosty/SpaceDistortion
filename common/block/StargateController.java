package jw.spacedistortion.common.block;

import jw.spacedistortion.common.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class StargateController extends Block {
	// The coordinate at which the textures for this block starts
	private int textureBegin;
	
	public StargateController(int id, int _textureBegin, Material material) {
		super(id, material);
		textureBegin = _textureBegin;
	}
	
	@Override
	public String getTextureFile() {
		return CommonProxy.TEXTURES_PNG;
	}
}
