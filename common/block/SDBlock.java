package jw.spacedistortion.common.block;

import jw.spacedistortion.StringGrid;
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
	public int[] detectStructure(World world, int xOrigin, int yOrigin, int zOrigin, StringGrid template) {
		int[] blocks;
		// For now, assume its on the xy plane
		// Move the template over each possible position
		match:
		for (int xOffset = 0; xOffset < template.width; xOffset++) {
			for (int yOffset = 0; yOffset < template.height; yOffset++) {
				// Test the template with this offset.
				// Do nothing for now; will call matchStructure
			}
		}
		return new int[]{};
	}
	
	// Try to match a structure using a StringGrid and the given position. If the match fails, blocks will
	// be null
	// TODO: Implement other axes (pl. axis)
	private boolean[][] matchStructure(World world, int x, int y, int z, StringGrid template) {
		// To keep track of the found blocks, if any
		boolean[][] blocks = new boolean[template.width][template.height];
		match:
		for (int xOffset = 0; xOffset < template.width; xOffset++) {
			for (int yOffset = 0; yOffset < template.height; yOffset++) {
				int id = world.getBlockId(x + xOffset, y + yOffset, z);
				if (template.get(xOffset, yOffset) != ' ') {
					// Expecting this block
					if (id == this.blockID) {
						// We matched a block on the structure, so add it to the list
						blocks[xOffset][yOffset] = true;
					} else {
						// This match evidently didn't work, so fail
						blocks = null;
					}
				} // No 'else' clause as blocks not part of the structure don't matter
			}
		}
		return blocks;
	}
	
	@Override
	public String getTextureFile() {
		return CommonProxy.TEXTURES_PNG;
	}
}
