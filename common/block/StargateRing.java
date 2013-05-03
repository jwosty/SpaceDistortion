package jw.spacedistortion.common.block;

import jw.spacedistortion.common.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class StargateRing extends Block {
	public StargateRing(int id, int texture, Material material) {
		super(id, texture, material);
	}
	
	@Override
	public String getTextureFile() {
		return CommonProxy.TEXTURES_PNG;
	}
}
