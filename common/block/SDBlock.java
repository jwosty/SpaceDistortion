package jw.spacedistortion.common.block;

import jw.spacedistortion.common.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class SDBlock extends Block {

	public static Block stargateRing = (new SDBlock(1600, 0, Material.rock))
			.setHardness(3.0f).setStepSound(Block.soundStoneFootstep)
			.setBlockName("stargateRing").setCreativeTab(CreativeTabs.tabBlock);
	public static Block stargateController = (new BlockStargateController(1601, 1))
			.setHardness(3.0f).setStepSound(Block.soundStoneFootstep)
			.setBlockName("stargateController").setCreativeTab(CreativeTabs.tabBlock);
	public static Block eventHorizon = (new BlockEventHorizon(1602, 16))
			.setHardness(-1.0f).setStepSound(Block.soundGlassFootstep)
			.setBlockName("eventHorizon").setLightValue(0.875f);
	
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
	
	// Returns 6 neighboring blocks on each of the faces (no corners here)
	public int[] getNeighboringBlocks(World world, int x, int y, int z) {
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
	
	// Returns all blocks in a structure if this block is part of it
	public int[] detectStructure(World world, int x, int y, int z, String... arrangement) {
		int width = 0;
		int height = arrangement.length;
		for (int i = 0; i < arrangement.length; i++) {
			int rowW = arrangement[i].length();
			if (width < rowW) {
				width = rowW;
			}
		}
	}
	
	@Override
	public String getTextureFile() {
		return CommonProxy.TEXTURES_PNG;
	}
}
