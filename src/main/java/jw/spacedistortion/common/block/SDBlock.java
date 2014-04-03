package jw.spacedistortion.common.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jw.spacedistortion.Axis;
import jw.spacedistortion.Pair;
import jw.spacedistortion.StringGrid;
import jw.spacedistortion.Triplet;
import jw.spacedistortion.common.CommonProxy;
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
			HashMap<Character, Pair<Block, Boolean>> charAndBlockDirectionMetadataKey) {
		DetectStructureResults results = null;
		// boolean[][] blocks = null;
		// For now, assume its on the xy plane
		// Move the template over each possible position
		match: for (Axis axis : Axis.values()) {
			for (int xOffset = 0; xOffset < template.width; xOffset++) {
				for (int yOffset = 0; yOffset < template.height; yOffset++) {
					if (template.get(xOffset, yOffset) != ' ') {
						// Test the template with this offset.
						Pair<Block[][], HashMap<Block, ForgeDirection>> blocksDirections = SDBlock.detectStructureAtLocation(
								world, template, xOrigin, yOrigin, zOrigin,
								axis, xOffset, yOffset, charAndBlockDirectionMetadataKey);
						Block[][] blocks = blocksDirections.X;
						HashMap<Block, ForgeDirection> blockDirections = blocksDirections.Y;
						if (blocks != null) {
							results = new DetectStructureResults(blocks, blockDirections,
									new Triplet(xOrigin, yOrigin, zOrigin), axis, xOffset, yOffset);
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
	 * @param axis
	 *            The axis the structure lies on (-1 = x-y, 0 = x-z, 1 = y-z)
	 * @param charAndBlockDirectionMetadataKey
	 *            A HashMap of characters to match with their blocks (and
	 *            whether or not the block has directional metadata) in the template
	 * @return
	 */
	public static Pair<Block[][], HashMap<Block, ForgeDirection>> detectStructureAtLocation(IBlockAccess world,
			StringGrid template, int x, int y, int z, Axis axis,
			int xTemplateOffset, int yTemplateOffset,
			HashMap<Character, Pair<Block, Boolean>> charAndBlockDirectionMetadataKey) {
		Triplet<Integer, Integer, Integer> p = new Triplet(x, y, z);
		// To keep track of the found blocks, if any
		Block[][] blocks = new Block[template.height][template.width];
		HashMap<Block, ForgeDirection> directionalMetadata = new HashMap();
		match:
			for (int gridY = 0; gridY < template.height; gridY++) {
			for (int gridX = 0; gridX < template.width; gridX++) {
				// Get the correct block
				Triplet<Integer, Integer, Integer> coords = SDBlock.getBlockInStructure(world, x, y, z,
						gridX - xTemplateOffset, -gridY + yTemplateOffset,
						axis);
				Block currentBlock = world.getBlock(coords.X, coords.Y, coords.Z);
				
				Character tChar = template.get(gridX, gridY);
				if (charAndBlockDirectionMetadataKey.containsKey(tChar)) {
					Pair<Block, Boolean> item = charAndBlockDirectionMetadataKey.get(tChar);
					Block templateBlock = item.X;
					if (currentBlock == templateBlock) {
						// Check for metadata
						if (item.Y) {
							ForgeDirection currentBlockDirection = ForgeDirection.getOrientation(world.getBlockMetadata(coords.X, coords.Y, coords.Z));
							if (directionalMetadata.containsKey(templateBlock)) {
								if (currentBlockDirection != directionalMetadata.get(templateBlock)) {
									// Metadata doesn't match, so fail
									blocks = null;
									break match;
								}
							} else {
								directionalMetadata.put(templateBlock, currentBlockDirection);
							}
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
		return new Pair(blocks, directionalMetadata);
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