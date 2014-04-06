package jw.spacedistortion.common.block;

import java.util.List;

import jw.spacedistortion.Pair;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

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
		boolean ret = super.removedByPlayer(world, player, x, y, z);
		List<Pair<Integer[], Block>> neighbors = this.getNeighboringBlocks(world, x, y, z);
		for (Pair<Integer[], Block> neighborAndCoords : neighbors) {
			Block neighbor = neighborAndCoords.Y;
			if (neighbor == SDBlock.eventHorizon) { 
				this.explode(world, player, x, y, z);
				break;
			}
		}
		return ret;
	}
	
	@Override
	public void onBlockDestroyedByExplosion(World world, int x, int y, int z, Explosion explosion) {
		if (!world.isRemote) {
			this.explode(world, null, x, y, z);
		}
	}
}