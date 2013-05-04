package jw.spacedistortion.common.block;

import jw.spacedistortion.common.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class SDBlock extends Block {

	public static Block stargateRing = (new SDBlock(1600, 0, Material.rock))
			.setHardness(3.0f).setStepSound(Block.soundStoneFootstep)
			.setBlockName("stargateRing").setCreativeTab(CreativeTabs.tabBlock);
	public static Block stargateController = (new BlockStargateController(1601, 1, Material.rock))
			.setHardness(3.0f).setStepSound(Block.soundStoneFootstep)
			.setBlockName("stargateController").setCreativeTab(CreativeTabs.tabBlock);
	public static Block eventHorizon = (new BlockEventHorizon(1602, 16, Material.portal))
			.setHardness(-1.0f).setStepSound(Block.soundGlassFootstep)
			.setBlockName("eventHorizon").setLightValue(0.75f);
	
	public static void addBlocks() {
		GameRegistry.registerBlock(stargateRing, "stargateRing");
		LanguageRegistry.addName(stargateRing, "Stargate Ring");
		GameRegistry.registerBlock(stargateController, "stargateController");
		LanguageRegistry.addName(stargateController, "Stargate Controller");
		// Don't need to set a tooltip name as this can't be obtained in the inventory without commands
		GameRegistry.registerBlock(eventHorizon, "eventHorizon");
	}

	public SDBlock(int id, int texture, Material material) {
		super(id, texture, material);
	}
	
	public SDBlock(int id, Material material) {
		super(id, material);
	}
	
	@Override
	public String getTextureFile() {
		return CommonProxy.TEXTURES_PNG;
	}
}
