package jw.spacedistortion.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockStargateRing extends SDBlock {
	public BlockStargateRing() {
		super(Material.rock);
	}
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		super.onBlockAdded(world, x, y, z);
		this.updateNearbyStargateControllers(world, x, y, z);
	}
	
	private void explode(World world, EntityLivingBase explosionCausingJerk, int x, int y, int z) {
		world.createExplosion(explosionCausingJerk, x, y, z, 8, true);
	}
	
	@Override
	public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z) {
		boolean result = super.removedByPlayer(world, player, x, y, z);
		if (!world.isRemote){ 
			for (ForgeDirection direction : ForgeDirection.VALID_DIRECTIONS) {
				if (world.getBlock(x + direction.offsetX, y + direction.offsetY, z + direction.offsetZ) == SDBlock.eventHorizon) { 
					this.explode(world, player, x, y, z);
					break;
				}
			}
		}
		return result;
	}
	
	@Override
	public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion) {
		super.onBlockDestroyedByExplosion(world, x, y, z, explosion);
		if (!world.isRemote) {
			this.explode(world, null, x, y, z);
		}
	}
}