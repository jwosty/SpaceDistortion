package jw.spacedistortion.common.item;

import jw.spacedistortion.client.SDSoundHandler;
import jw.spacedistortion.common.CommonProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemStaffWeapon extends SDItem {
	public ItemStaffWeapon() {
		this.setMaxStackSize(1);
		this.setMaxDamage(100);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		stack.damageItem(1, player);
		if (!world.isRemote) {
			Vec3 look = player.getLookVec();
			EntityLargeFireball fireball = new EntityLargeFireball(world, player, 1, 1, 1);
			fireball.setPosition(
					player.posX + look.xCoord * 2,
					player.posY + look.yCoord + 1,
					player.posZ + look.zCoord * 2);
			fireball.accelerationX = look.xCoord * 0.1;
			fireball.accelerationY = look.yCoord * 0.1;
			fireball.accelerationZ = look.zCoord * 0.1;
			world.spawnEntityInWorld(fireball);
			
			
			SDSoundHandler.serverPlaySoundToPlayers(world.playerEntities, "weapons.staff", 1F, 0.9F + (itemRand.nextFloat() * 0.2F),
						player.posX, player.posY, player.posZ);
		}
		if (stack.getItemDamage() < stack.getMaxDamage()) {
			return stack;
		} else {
			return new ItemStack(SDItem.depletedStaffWeapon);
		}
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFull3D() {
		GL11.glRotatef(-90, 1, 0, 0);
		GL11.glTranslatef(0F, 0.25F, 0.4F);
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldRotateAroundWhenRendering() {
		return false;
	}
	
	@Override
	public String getIconString() {
		return CommonProxy.MOD_ID + ":depletedStaffWeapon";
	}
}
