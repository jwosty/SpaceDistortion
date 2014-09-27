package jw.spacedistortion.common.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemStaffWeapon extends SDItem {
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
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
		}
		return stack;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean isFull3D() {
		return true;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldRotateAroundWhenRendering() {
		return false;
	}
}
