package jw.spacedistortion.common.block;

import java.util.ArrayList;
import java.util.List;

import jw.spacedistortion.StringGrid;
import jw.spacedistortion.common.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.World;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

public class SDBlock extends Block {
	public static Block stargateRing = (new BlockStargateRing(1600, 0))
			.setHardness(3.0f).setStepSound(Block.soundStoneFootstep)
			.setBlockName("stargateRing").setCreativeTab(CreativeTabs.tabBlock);
	public static Block stargateController = (new BlockStargateController(1601,
			1)).setHardness(3.0f).setStepSound(Block.soundStoneFootstep)
			.setBlockName("stargateController")
			.setCreativeTab(CreativeTabs.tabBlock);
	public static Block eventHorizon = (new BlockEventHorizon(1602, 16))
			.setHardness(-1.0f).setStepSound(Block.soundGlassFootstep)
			.setBlockName("eventHorizon").setLightValue(0.875f);

	public static void addBlocks() {
		GameRegistry.registerBlock(stargateRing, "stargateRing");
		LanguageRegistry.addName(stargateRing, "Stargate Ring");
		GameRegistry.registerBlock(stargateController, "stargateController");
		LanguageRegistry.addName(stargateController, "Stargate Controller");
		// Don't need to set a tooltip name as this can't be obtained in the
		// inventory without commands
		GameRegistry.registerBlock(eventHorizon, "eventHorizon");
	}

	public SDBlock(int id, int texture, Material material) {
		super(id, texture, material);
	}

	public SDBlock(int id, Material material) {
		super(id, material);
	}

	// Returns 6 neighboring blocks on each of the faces (no corners here)
	/**
	 * @param world
	 *            The world in which to find the blocks
	 * @param x
	 *            The x coordinate
	 * @param y
	 *            The y coordinate
	 * @param z
	 *            The z coordinate
	 * @return A list of blocks in the format of a list where the first 3
	 *         elements are the x, y, and z and the last is the block id
	 */
	public List<Integer[]> getNeighboringBlocks(World world, int x, int y, int z) {
		List<Integer[]> blocks = new ArrayList<Integer[]>();
		int[][] neighbors = { { -1, 0, 0 }, { 1, 0, 0 }, { 0, -1, 0 },
				{ 0, 1, 0 }, { 0, 0, -1 }, { 0, 0, 1 } };
		for (int i = 0; i < neighbors.length; i++) {
			// Get information about the block
			int[] neighbor = neighbors[i];
			int bx = neighbor[0] + x;
			int by = neighbor[1] + y;
			int bz = neighbor[2] + z;
			int id = world.getBlockId(bx, by, bz);
			if (id != 0) {
				// We found a neighbor, so add it to the list
				blocks.add(new Integer[] { bx, by, bz, id });
			}
		}
		return blocks;
	}

	// Returns all blocks in a structure if this block is part of it
	public DetectStructureResults detectStructure(World world,
			StringGrid template, int xOrigin, int yOrigin, int zOrigin) {
		DetectStructureResults results = null;
		// boolean[][] blocks = null;
		// For now, assume its on the xy plane
		// Move the template over each possible position
		match: for (int axis = -1; axis < 2; axis++) {
			for (int xOffset = 0; xOffset < template.width; xOffset++) {
				for (int yOffset = 0; yOffset < template.height; yOffset++) {
					if (template.get(xOffset, yOffset) != ' ') {
						// Test the template with this offset.
						boolean[][] blocks = this.detectStructureAtLocation(
								world, template, xOrigin, yOrigin, zOrigin,
								axis, xOffset, yOffset);
						if (blocks != null) {
							results = new DetectStructureResults(blocks, axis,
									xOffset, yOffset);
							break match;
						}
					}
				}
			}
		}
		return results;
	}

	/**
	 * Find a structure using a StringGrid and the given position. If no
	 * structure is found at the location provided, return value will be null
	 * 
	 * @param world
	 *            The world to detect the structure in
	 * @param template
	 *            The structure to detect
	 * @param x
	 *            The x coordinate of the top-left corner of the structure
	 * @param y
	 *            The y ...
	 * @param z
	 *            The z ...
	 * @param plane
	 *            The plane the structure lies on (-1 = x-y, 0 = x-z, 1 = y-z)
	 * @param xFlip
	 *            If true, the structure is mirrored
	 * @return
	 */
	public boolean[][] detectStructureAtLocation(World world,
			StringGrid template, int x, int y, int z, int plane,
			int xTemplateOffset, int yTemplateOffset) {
		// To keep track of the found blocks, if any
		boolean[][] blocks = new boolean[template.height][template.width];
		match: for (int gridY = 0; gridY < template.height; gridY++) {
			for (int gridX = 0; gridX < template.width; gridX++) {
				// Get the correct block
				int[] coords = this.getBlockInStructure(world, x, y, z, gridX
						- xTemplateOffset, -gridY + yTemplateOffset, plane);
				int id = world.getBlockId(coords[0], coords[2], coords[3]);
				// Test it
				if (template.get(gridX, gridY) != ' ') {
					// Expecting this block
					if (id == this.blockID) {
						// We matched a block on the structure, so add it to the
						// list
						blocks[gridY][gridX] = true;
					} else {
						// This match evidently didn't work, so fail
						blocks = null;
						break match;
					}
				} // No 'else' clause as blocks not part of the structure don't
					// matter
			}
		}
		return blocks;
	}

	protected int[] getBlockInStructure(World world, int x, int y, int z,
			int gridX, int gridY, int plane) {
		int bx;
		int by;
		int bz;
		if (plane == -1) {
			bx = x + gridX;
			by = y + gridY;
			bz = z;
		} else if (plane == 0) {
			bx = x + gridX;
			by = y;
			bz = z + gridY;
		} else if (plane == 1) {
			bx = x;
			by = y + gridY;
			bz = z + gridX;
		} else {
			throw new IllegalArgumentException("Bad orientation value of "
					+ plane);
		}
		return new int[] { bx, by, bz };
	}

	@Override
	public String getTextureFile() {
		return CommonProxy.TEXTURES_PNG;
	}
}
