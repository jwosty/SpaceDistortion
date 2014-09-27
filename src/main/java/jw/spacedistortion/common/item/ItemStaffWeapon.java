package jw.spacedistortion.common.item;

import org.lwjgl.opengl.GL11;

import jw.spacedistortion.client.SDSoundHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSound;
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
		stack.damageItem(10, player);
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
		return stack;
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
}
