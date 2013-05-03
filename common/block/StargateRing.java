package jw.spacedistortion.common.block;

import jw.taw.common.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

public class StargateRing extends Block {
	public StargateRing(int id, Material texture) {
		super(id, texture);
	}
	
	@Override
	public String getTextureFile() {
		return CommonProxy.TEXTURES_PNG;
	}
}
