package jw.spacedistortion.common.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import jw.spacedistortion.Axis;
import jw.spacedistortion.Pair;
import jw.spacedistortion.StringGrid;
import jw.spacedistortion.Triplet;
import jw.spacedistortion.common.CommonProxy;
import jw.spacedistortion.common.SpaceDistortion;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SDBlock extends Block {
	public static BlockStargateController stargateController;
	public static BlockStargateRing stargateRing;
	public static BlockStargateRingChevron stargateRingChevron;
	public static BlockEventHorizon eventHorizon;

	/**
	 * Create the block objects based on configuration information
	 * @param config The configuration file to use
	 */
	public static void configureBlocks(Configuration config) {
		stargateController = (BlockStargateController) new BlockStargateController()
				.setBlockName("stargateController")
				.setCreativeTab(CreativeTabs.tabBlock)
				.setStepSound(Block.soundTypeStone);
		stargateRing = (BlockStargateRing) new BlockStargateRing()
				.setBlockName("stargateRing")
				.setCreativeTab(CreativeTabs.tabBlock)
				.setStepSound(Block.soundTypeStone);
		stargateRingChevron = (BlockStargateRingChevron) new BlockStargateRingChevron()
				.setBlockName("stargateRingChevron")
				.setCreativeTab(CreativeTabs.tabBlock)
				.setStepSound(Block.soundTypeStone);
		eventHorizon = (BlockEventHorizon) new BlockEventHorizon()
				.setBlockName("eventHorizon")
				.setStepSound(Block.soundTypeGlass)
				.setLightLevel(0.875f);
	}
	
	public SDBlock(Material material) {
		super(material);
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
	public void registerBlockIcons(IIconRegister register) {
		this.blockIcon = register.registerIcon(this.getIconName());
	}
	
	/**
	 * Registers all blocks in the mod and the names for the blocks
	 */
	public static void registerBlocks() {
		GameRegistry.registerBlock(stargateController, stargateController.getUnlocalizedName());
		GameRegistry.registerBlock(stargateRing, stargateRing.getUnlocalizedName());
		GameRegistry.registerBlock(stargateRingChevron, stargateRingChevron.getUnlocalizedName());
		GameRegistry.registerBlock(eventHorizon, eventHorizon.getUnlocalizedName());
	}

	/**
	 * Synchronizes a TileEntity with all clients. Only works server-side; will throw an error for client-side!
	 * @param tileEntity
	 */
	public static void syncTileEntity(TileEntity tileEntity) {
		if (tileEntity != null) {
			Packet packet = tileEntity.getDescriptionPacket();
			
			//PacketDispatcher.sendPacketToAllPlayers(packet);
		}
	}
	
	public void updateNearbyStargateControllers(World world, int x, int y, int z) {
		Block thisBlock = world.getBlock(x, y, z);
		for (int xx = (x-4); xx < (x+5); xx++) {
			for (int yy = (y-4); yy < (y+5); yy++) {
				for (int zz = (z-4); zz < (z+5); zz++) {
					Block block = world.getBlock(xx, yy, zz);
					if (block == SDBlock.stargateController && !(world.isRemote)) {
						world.notifyBlockOfNeighborChange(xx, yy, zz, SDBlock.stargateController);
						world.markBlockForUpdate(xx, yy, zz);
						SDBlock.syncTileEntity(world.getTileEntity(xx, yy, zz));
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
	public static List<Pair<Integer[], Block>> getNeighboringBlocks(IBlockAccess world, int x,
			int y, int z) {
		List<Pair<Integer[], Block>> blocks = new ArrayList<Pair<Integer[], Block>>();
		int[][] neighbors = { { -1, 0, 0 }, { 1, 0, 0 }, { 0, -1, 0 },
				{ 0, 1, 0 }, { 0, 0, -1 }, { 0, 0, 1 } };
		for (int i = 0; i < neighbors.length; i++) {
			// Get information about the block
			int[] neighbor = neighbors[i];
			int bx = neighbor[0] + x;
			int by = neighbor[1] + y;
			int bz = neighbor[2] + z;
			Block block = world.getBlock(bx, by, bz);
			if (block != Blocks.air) {
				// We found a neighbor, so add it to the list
				blocks.add(new Pair(new Integer[] { bx, by, bz }, block ));
			}
		}
		return blocks;
	}
	
	// Returns all blocks in a structure if this block is part of it
	public static DetectStructureResults detectStructure(IBlockAccess world,
			StringGrid template, int xOrigin, int yOrigin, int zOrigin,
			HashMap<Character, Pair<Block, Boolean>> charToBlock) {
		
		// Initialize a clone of the character to block and direction HashMap, using directions when needed
		HashMap<Character, Pair<Block, Integer>> charToBlockAndMetadata = new HashMap();
		for (Entry<Character, Pair<Block, Boolean>> e : charToBlock.entrySet()) {
			charToBlockAndMetadata.put(e.getKey(), new Pair(e.getValue().X, e.getValue().Y ? ForgeDirection.UNKNOWN : null));
		}
		
		DetectStructureResults results = null;
		// Move the template over each possible position
		match: for (ForgeDirection facing : ForgeDirection.VALID_DIRECTIONS) {
			// Update metadata
			for (Entry<Character, Pair<Block, Integer>> e : charToBlockAndMetadata.entrySet()) {
				if (e.getValue().Y != null) {
					charToBlockAndMetadata.put(e.getKey(), new Pair(e.getValue().X, facing.ordinal()));
				}
			}
			for (int xOffset = 0; xOffset < template.width; xOffset++) {
				for (int yOffset = 0; yOffset < template.height; yOffset++) {
					// It's pointless to test for a structure here if the current block match is a wildcard
					if (charToBlockAndMetadata.containsKey(template.get(xOffset, yOffset))) {
						// Test the template with this offset.
						Block[][] blocks = SDBlock.detectStructureAtLocation(
								world, template, xOrigin, yOrigin, zOrigin,
								facing, xOffset, yOffset, charToBlockAndMetadata);
						if (blocks != null) {
							results = new DetectStructureResults(
									blocks, new Triplet(xOrigin, yOrigin, zOrigin),
									facing, xOffset, yOffset);
							break match;
						}
					}
				}
			}
		}
		return results;
	}

	/**
	 * Find a structure using a StringGrid and the given position. Optionally,
	 * check if the directional metadata of certain blocks is the same and
	 * include this in the result. If no structure matches at the location
	 * provided or metadata of specified blocks is not all the same, the
	 * return value will be null
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
	 * @param facing
	 *            The direction the structure is facing
	 * @param charAndBlockDirectionMetadataKey
	 *            A HashMap of characters to match with their blocks (and
	 *            whether or not the block has directional metadata) in the template
	 * @return
	 */
	public static Block[][] detectStructureAtLocation(IBlockAccess world,
			StringGrid template, int x, int y, int z, ForgeDirection facing,
			int xTemplateOffset, int yTemplateOffset,
			HashMap<Character, Pair<Block, Integer>> charToBlockAndMetadata) {
		
		Triplet<Integer, Integer, Integer> p = new Triplet(x, y, z);
		// To keep track of the found blocks, if any
		Block[][] blocks = new Block[template.height][template.width];
		match:
			for (int gridY = 0; gridY < template.height; gridY++) {
				for (int gridX = 0; gridX < template.width; gridX++) {
					// Find the position of the block in the structure
					Triplet<Integer, Integer, Integer> coords = SDBlock.getBlockInStructure(world, x, y, z,
							gridX - xTemplateOffset, -gridY + yTemplateOffset,
							facing);
					//Triplet<Integer, Integer, Integer> coords = new Triplet(x + facing.offsetX, y + facing.offsetY, z + facing.offsetZ);
					Block currentBlock = world.getBlock(coords.X, coords.Y, coords.Z);
					
					Character tChar = template.get(gridX, gridY);
					if (charToBlockAndMetadata.containsKey(tChar)) {
						Pair<Block, Integer> bm = charToBlockAndMetadata.get(tChar);
						Block templateBlock = bm.X;
						Integer metadataTemplate = bm.Y;
						if (currentBlock == templateBlock) {
							// Check for metadata
							if (metadataTemplate != null && world.getBlockMetadata(coords.X, coords.Y, coords.Z) != metadataTemplate) {
								// Metadata doesn't match template, so fail
								blocks = null;
								break match;
							}
							// This block matches the template, so add it to the results
							blocks[gridX][gridY] = currentBlock;
							ForgeDirection d = ForgeDirection.getOrientation(0);
						} else {
							// Block doesn't match, so fail
							blocks = null;
							break match;
						}
					} else {
						// Treat any character not in charBlockKey as a wildcard
						blocks[gridX][gridY] = currentBlock;
					}
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
			ForgeDirection facing) {
		Triplet<Integer, Integer, Integer> offset = templateToWorldCoordinates(gridX, gridY, facing);
		return new Triplet(x + offset.X, y + offset.Y, z + offset.Z);
	}
	
	public static Triplet<Integer, Integer, Integer> templateToWorldCoordinates(int tx, int ty, ForgeDirection facing) {
		int x = 0;
		int y = 0;
		int z = 0;
		
		switch (facing) {
		case NORTH:
			x = -tx;
			y = ty;
			break;
		case SOUTH:
			x = tx;
			y = ty;
			break;
		case EAST:
			y = ty;
			z = -tx;
			break;
		case WEST:
			y = ty;
			z = tx;
			break;
		case UP:
			x = tx;
			z = ty;
			break;
		case DOWN:
			x = -tx;
			z = ty;
			break;
		case UNKNOWN:
			throw new RuntimeException("Unknown forge direction");
		}
		
		return new Triplet(x, y, z);
	}
}