package jw.spacedistortion.common.block;

import jw.spacedistortion.common.CommonProxy;
import jw.spacedistortion.common.item.SDItem;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SDBlock extends Block {
	public static SDBlock naquadahOre;
	public static BlockStargateController stargateController;
	public static BlockStargateRing stargateRing;
	public static BlockStargateRingChevron stargateRingChevron;
	public static BlockEventHorizon eventHorizon;
	public static BlockRingPlatform ringPlatform;

	/**
	 * Create the block objects based on configuration information
	 * @param config The configuration file to use
	 */
	public static void configureBlocks(Configuration config) {
		naquadahOre = (SDBlock) new SDBlock(Material.rock)
				.setBlockName("naquadahOre")
				.setCreativeTab(CreativeTabs.tabBlock)
				.setStepSound(Block.soundTypeStone)
				.setHardness(3).setResistance(10F);;
		naquadahOre.setHarvestLevel("pickaxe", 2, 0);
		stargateController = (BlockStargateController) new BlockStargateController()
				.setBlockName("stargateController")
				.setCreativeTab(CreativeTabs.tabBlock)
				.setStepSound(Block.soundTypeStone)
				.setHardness(2.5F).setResistance(10F);
		stargateController.setHarvestLevel("pickaxe", 1, 0);
		stargateRing = (BlockStargateRing) new BlockStargateRing()
				.setBlockName("stargateRing")
				.setCreativeTab(CreativeTabs.tabBlock)
				.setStepSound(Block.soundTypeStone)
				.setHardness(3.5F).setResistance(10F);
		stargateController.setHarvestLevel("pickaxe", 1, 0);
		stargateRingChevron = (BlockStargateRingChevron) new BlockStargateRingChevron()
				.setBlockName("stargateRingChevron")
				.setCreativeTab(CreativeTabs.tabBlock)
				.setStepSound(Block.soundTypeStone)
				.setHardness(3.5F).setResistance(10F);
		stargateRingChevron.setHarvestLevel("pickaxe", 1, 0);
		eventHorizon = (BlockEventHorizon) new BlockEventHorizon()
				.setBlockName("eventHorizon")
				.setStepSound(Block.soundTypeGlass)
				.setLightLevel(0.875f)
				.setBlockUnbreakable().setResistance(6000000F);
		ringPlatform = (BlockRingPlatform) new BlockRingPlatform()
				.setBlockName("ringPlatform")
				.setCreativeTab(CreativeTabs.tabBlock)
				.setStepSound(Block.soundTypeStone)
				.setHardness(3F).setResistance(10F);
		ringPlatform.setHarvestLevel("pickaxe", 1, 0);
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
		GameRegistry.registerBlock(naquadahOre, "naquadahOre");
		GameRegistry.registerBlock(stargateController, "stargateController");
		GameRegistry.registerBlock(stargateRing, "stargateRing");
		GameRegistry.registerBlock(stargateRingChevron, "stargateRingChevron");
		GameRegistry.registerBlock(eventHorizon, "eventHorizon");
		GameRegistry.registerBlock(ringPlatform, "ringPlatform");
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
					}
				}
			}
		}
	}
}