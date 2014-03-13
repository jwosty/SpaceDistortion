package jw.spacedistortion.common.block;

import java.util.ArrayList;
import java.util.List;

import jw.spacedistortion.Axis;
import jw.spacedistortion.StringGrid;
import jw.spacedistortion.Triplet;
import jw.spacedistortion.common.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.network.NetServerHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SDBlock extends Block {
	public static BlockStargateRing stargateRing;
	public static BlockStargateController stargateController;
	public static BlockEventHorizon eventHorizon;

	/**
	 * Create the block objects based on configuration information
	 * @param config The configuration file to use
	 */
	public static void configureBlocks(Configuration config) {
		stargateRing = (BlockStargateRing) new BlockStargateRing(config.get(
				"Blocks", "Stargate Ring", 1600).getInt())
				.setUnlocalizedName("stargateRing")
				.setCreativeTab(CreativeTabs.tabBlock)
				.setStepSound(Block.soundStoneFootstep);
		stargateController = (BlockStargateController) new BlockStargateController(
				config.get("Blocks", "Stargate Controller", 1601).getInt())
				.setUnlocalizedName("stargateController")
				.setCreativeTab(CreativeTabs.tabBlock)
				.setStepSound(Block.soundStoneFootstep);
		eventHorizon = (BlockEventHorizon) new BlockEventHorizon(config.get(
				"Blocks", "Event Horizon", 1602).getInt())
				.setUnlocalizedName("eventHorizon")
				.setStepSound(Block.soundGlassFootstep).setLightValue(0.875f);
	}

	/**
	 * For use in registerIcons, but doesn't actually register the icon and just
	 * returns the icon name
	 * 
	 * @param side The side of the block
	 * @param metadata Block metadata
	 * @return The icon name (for use in registerIcon)
	 */
	public String getIconName() {
		return CommonProxy.MOD_ID + ":" + (this.getUnlocalizedName().substring(5));
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister register) {
		this.blockIcon = register.registerIcon(this.getIconName());
	}
	
	/**
	 * Registers all blocks in the mod and the names for the blocks
	 */
	public static void registerBlocks() {
		GameRegistry.registerBlock(stargateRing, stargateRing.getUnlocalizedName());
		LanguageRegistry.addName(stargateRing, "Stargate Ring");
		GameRegistry.registerBlock(stargateController, "stargateController");
		LanguageRegistry.addName(stargateController, "Stargate Controller");
		// Don't need to set a tooltip name as this can't be obtained in the inventory without commands
		GameRegistry.registerBlock(eventHorizon, "eventHorizon");
	}
	
	public SDBlock(int id, Material material) {
		super(id, material);
	}

	/**
	 * Synchronizes a TileEntity with all clients. Only works server-side; will throw an error for client-side!
	 * @param tileEntity
	 */
	public static void syncTileEntity(TileEntity tileEntity) {
		if (tileEntity != null) {
			Packet packet = tileEntity.getDescriptionPacket();
			PacketDispatcher.sendPacketToAllPlayers(packet);
		}
	}
	
	public void updateNearbyStargateControllers(World world, int x, int y, int z) {
		int thisBlock = world.getBlockId(x, y, z);
		for (int xx = (x-4); xx < (x+5); xx++) {
			for (int yy = (y-4); yy < (y+5); yy++) {
				for (int zz = (z-4); zz < (z+5); zz++) {
					int blockID = world.getBlockId(xx, yy, zz);
					if (blockID == SDBlock.stargateController.blockID) {
						world.notifyBlockOfNeighborChange(xx, yy, zz, SDBlock.stargateController.blockID);
					}
				}
			}
		}
	}
	
	/**
	 * Returns 6 neighboring blocks on each of the faces, not including the corners
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
	public static List<Integer[]> getNeighboringBlocks(IBlockAccess world, int x,
			int y, int z) {
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
	public static DetectStructureResults detectStructure(IBlockAccess world,
			StringGrid template, int xOrigin, int yOrigin, int zOrigin,
			int blockID) {
		DetectStructureResults results = null;
		// boolean[][] blocks = null;
		// For now, assume its on the xy plane
		// Move the template over each possible position
		match: for (Axis axis : Axis.values()) {
			for (int xOffset = 0; xOffset < template.width; xOffset++) {
				for (int yOffset = 0; yOffset < template.height; yOffset++) {
					if (template.get(xOffset, yOffset) != ' ') {
						// Test the template with this offset.
						boolean[][] blocks = SDBlock.detectStructureAtLocation(
								world, template, xOrigin, yOrigin, zOrigin,
								axis, xOffset, yOffset, blockID);
						if (blocks != null) {
							results = new DetectStructureResults(blocks,
									new Triplet(xOrigin, yOrigin, zOrigin),
									axis, xOffset, yOffset);
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
	 * @return
	 */
	public static boolean[][] detectStructureAtLocation(IBlockAccess world,
			StringGrid template, int x, int y, int z, Axis axis,
			int xTemplateOffset, int yTemplateOffset,
			int blockID) {
		Triplet<Integer, Integer, Integer> p = new Triplet(x, y, z);
		// To keep track of the found blocks, if any
		boolean[][] blocks = new boolean[template.height][template.width];
		match:
			for (int gridY = 0; gridY < template.height; gridY++) {
			for (int gridX = 0; gridX < template.width; gridX++) {
				// Get the correct block
				Triplet<Integer, Integer, Integer> coords = SDBlock.getBlockInStructure(world, x, y, z,
						gridX - xTemplateOffset, -gridY + yTemplateOffset,
						axis);
				int id = world.getBlockId(coords.X, coords.Y, coords.Z);
				// Test it
				if (template.get(gridX, gridY) != ' ') {
					// Expecting this block
					if (id == blockID) {
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
	
	/**
	 * Get a set of block coordinates relative to a structure.
	 * 
	 * @param x
	 *            The x origin of the structure
	 * @param y
	 *            The y origin of the structure
	 * @param z
	 *            The z origin of the structure
	 * @param gridX
	 *            The x position in the structure
	 * @param gridY
	 *            The y position in the structure
	 * @param axis
	 *            The axis perpendicular to the structure
	 * @param a
	 *            The angle of the structure in radians
	 * @return
	 */
	public static Triplet<Integer, Integer, Integer> getBlockInStructure(
			IBlockAccess world, int x, int y, int z, int gridX, int gridY,
			Axis axis) {
		Triplet<Integer, Integer, Integer> offset = templateToWorldCoordinates(gridX, gridY, axis);
		return new Triplet(x + offset.X, y + offset.Y, z + offset.Z);
	}
	
	public static Triplet<Integer, Integer, Integer> templateToWorldCoordinates(int tx, int ty, Axis axis) {
		int x;
		int y;
		int z;
		if (axis == Axis.X) {
			x = 0;
			y = ty;
			z = tx;
		} else if (axis == Axis.Y) {
			x = tx;
			y = 0;
			z = ty;
		} else { // axis == Axis.Z
			x = tx;
			y = ty;
			z = 0;
		}
		return new Triplet(x, y, z);
	}
}