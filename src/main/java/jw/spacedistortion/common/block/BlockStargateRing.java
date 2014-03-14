package jw.spacedistortion.common.block;

import net.minecraft.block.material.Material;
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
}