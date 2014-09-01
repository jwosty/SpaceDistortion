package jw.spacedistortion.common.item;

import java.util.List;

import jw.spacedistortion.common.CommonProxy;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemControlCrystal extends SDItem {
	private IIcon[] icons;
	private String[] types = new String[] { "colorless", "red", "yellow", "green", "blue" };
	
	public ItemControlCrystal() {
		this.setHasSubtypes(true);
		this.setMaxDamage(0);
		this.setCreativeTab(CreativeTabs.tabMaterials);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack itemStack) {
		return super.getUnlocalizedName() + "." + this.types[MathHelper.clamp_int(itemStack.getItemDamage(), 0, 5)];
	}
	
	@Override
	public void getSubItems(Item control_crystal, CreativeTabs tab, List items) {
		for (int i = 0; i < 5; i++) {
			items.add(new ItemStack(control_crystal, 1, i));
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister) {
		this.icons = new IIcon[5];
		for (int i = 0; i < 5; i++) {
			this.icons[i] = iconRegister.registerIcon(this.getIconString() + "_" + this.types[i]);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int damage) {
		return this.icons[MathHelper.clamp_int(damage, 0, 4)];
	}
}
