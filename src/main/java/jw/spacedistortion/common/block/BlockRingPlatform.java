package jw.spacedistortion.common.block;

import jw.spacedistortion.common.SpaceDistortion;
import jw.spacedistortion.common.entity.EntityTransporterRings;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockRingPlatform extends SDBlock {
	public BlockRingPlatform() {
		super(Material.rock);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int par1, float par2, float par3, float par4) {
		// Dont start a transport if one is already in progress
		if (world.getEntitiesWithinAABB(EntityTransporterRings.class, AxisAlignedBB.getBoundingBox(x - 1, y, z - 1, x + 1, y + 2, z + 1)).size() > 0) {
			return true;
		}
		Structure s = Structure.detectStructure(world, x, y, z, SpaceDistortion.transporterRingsShape, SpaceDistortion.templateBlockInfo, ForgeDirection.UP);
		if (s != null) {
			world.spawnEntityInWorld(new EntityTransporterRings(world, s.x + 2, s.y + 1, s.z + 2));
			return true;
		} else {
			return false;
		}
	}
}