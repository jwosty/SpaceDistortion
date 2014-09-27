package jw.spacedistortion.common.item;

import jw.spacedistortion.common.CommonProxy;
import jw.spacedistortion.common.block.SDBlock;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SDItem extends Item {
	public static Item naquadahIngot;
	public static Item naquadahCircuit;
	public static Item controlCrystal;
	public static Item liquefiedNaquadah;
	public static Item depletedStaffWeapon;
	public static Item staffWeapon;
	
	public static void registerItems() {
		naquadahIngot = new SDItem().setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("naquadahIngot");
		naquadahCircuit = new SDItem().setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("naquadahCircuit");
		controlCrystal = new ItemControlCrystal().setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("controlCrystal");
		liquefiedNaquadah = new SDItem().setCreativeTab(CreativeTabs.tabMaterials).setUnlocalizedName("liquefiedNaquadah");
		depletedStaffWeapon = new SDItem().setCreativeTab(CreativeTabs.tabCombat).setUnlocalizedName("depletedStaffWeapon").setMaxStackSize(1);
		staffWeapon = new ItemStaffWeapon().setCreativeTab(CreativeTabs.tabCombat).setUnlocalizedName("staffWeapon");
		GameRegistry.registerItem(naquadahIngot, "naquadahIngot");
		GameRegistry.registerItem(naquadahCircuit, "naquadahCircuit");
		GameRegistry.registerItem(controlCrystal, "controlCrystal");
		GameRegistry.registerItem(liquefiedNaquadah, "liquefiedNaquadah");
		GameRegistry.registerItem(depletedStaffWeapon, "depletedStaffWeapon");
		GameRegistry.registerItem(staffWeapon, "staffWeapon");
	}
	
	public static void registerRecipes() {
		ItemStack stone = new ItemStack(Blocks.stone);
		ItemStack iron = new ItemStack(Items.iron_ingot);
		ItemStack redstone = new ItemStack(Items.redstone);
		ItemStack naquadah = new ItemStack(naquadahIngot);
		ItemStack lnaquadah = new ItemStack(liquefiedNaquadah);
		ItemStack circuit = new ItemStack(naquadahCircuit);
		
		// Control crystals
		GameRegistry.addRecipe(new ItemStack(controlCrystal, 1, 0), " d ", "gdg", "gdg",
				'g', new ItemStack(Blocks.glass), 'd', new ItemStack(Items.diamond));
		GameRegistry.addShapelessRecipe(new ItemStack(controlCrystal, 1, 1), new ItemStack(controlCrystal), new ItemStack(Items.dye, 1, 1));
		GameRegistry.addShapelessRecipe(new ItemStack(controlCrystal, 1, 2), new ItemStack(controlCrystal), new ItemStack(Items.dye, 1, 11));
		GameRegistry.addShapelessRecipe(new ItemStack(controlCrystal, 1, 3), new ItemStack(controlCrystal), new ItemStack(Items.dye, 1, 2));
		GameRegistry.addShapelessRecipe(new ItemStack(controlCrystal, 1, 4), new ItemStack(controlCrystal), new ItemStack(Items.dye, 1, 4));

		// Naquadah stuff
		GameRegistry.addSmelting(SDBlock.naquadahOre, new ItemStack(naquadahIngot), 5);
		GameRegistry.addRecipe(new ItemStack(naquadahCircuit), "ini", "rrr", "ini", 'i', iron, 'n', naquadah, 'r', redstone);
		GameRegistry.addShapelessRecipe(lnaquadah,
				naquadah, naquadah, new ItemStack(Items.blaze_powder), new ItemStack(Items.glass_bottle));
		
		// Stargate stuff
		GameRegistry.addRecipe(new ItemStack(SDBlock.stargateRing), "sis", "sns", "sns", 's', stone, 'i', iron, 'n', circuit);
		// dye 14 = orange dye
		GameRegistry.addRecipe(new ItemStack(SDBlock.stargateRingChevron), "rdg",
				'r', new ItemStack(SDBlock.stargateRing), 'd', new ItemStack(Items.dye, 1, 14), 'g', new ItemStack(Blocks.glass));
		GameRegistry.addRecipe(new ItemStack(SDBlock.stargateController), "srs", "ygb", "scs",
				's', stone, 'c', circuit,
				'r', new ItemStack(controlCrystal, 1, 1), 'y', new ItemStack(controlCrystal, 1, 2),
				'g', new ItemStack(controlCrystal, 1, 3), 'b', new ItemStack(controlCrystal, 1, 4));
		
		// Transporter ring platform
		GameRegistry.addRecipe(new ItemStack(SDBlock.ringPlatform), "isi", "ncn", "sss",
				'i', iron, 'n', naquadah, 's', stone, 'c', new ItemStack(controlCrystal));
		
		// Weapons
		GameRegistry.addRecipe(new ItemStack(depletedStaffWeapon), " f ", " n ", "ici",
				'f', new ItemStack(Items.flint_and_steel), 'n', circuit, 'i', iron, 'c', new ItemStack(controlCrystal, 1, 4));
		GameRegistry.addShapelessRecipe(new ItemStack(staffWeapon), new ItemStack(depletedStaffWeapon), lnaquadah);
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public String getIconString() {
		return CommonProxy.MOD_ID + ":" + (this.getUnlocalizedName().substring(5));
	}
}
