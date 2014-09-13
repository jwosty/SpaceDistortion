package jw.spacedistortion.common.block;

import jw.spacedistortion.common.entity.EntityTransporterRings;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class BlockRingPlatform extends SDBlock {
	public BlockRingPlatform() {
		super(Material.rock);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int par1, float par2, float par3, float par4) {
		Entity rings = new EntityTransporterRings(world, x, y + 1, z);
		world.spawnEntityInWorld(rings);
		return true;
	}
}