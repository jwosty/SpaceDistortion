package jw.spacedistortion.common.item;

import jw.spacedistortion.common.SpaceDistortion;
import jw.spacedistortion.common.block.Structure;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemStargateRingDisintegrator extends SDItem {
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float float1, float float2, float float3) {
		Structure s = Structure.detectStructure(world, x, y, z, SpaceDistortion.stargateRingShape, SpaceDistortion.templateBlockInfo);
		if (s != null) {
			if (!world.isRemote) {
				s.removeFromWorld(world);
			}
			return true;
		}
		return false;
	}
}
