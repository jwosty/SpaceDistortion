package jw.spacedistortion.common.item;


import jw.spacedistortion.common.SpaceDistortion;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemStargateRingFormer extends SDItem {
	@Override
	public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float float1, float float2, float float3) {
		if (!world.isRemote) {
			ForgeDirection fside = ForgeDirection.getOrientation(side);
			int bx = x + fside.offsetX;
			int by = y + fside.offsetY + 4;
			int bz = z + fside.offsetZ;
			SpaceDistortion.goauldBaseGen.generate(this.itemRand, x, y + 4, z, world);
			/*
			new Structure(bx, by, bz, ForgeDirection.getOrientation(BlockPistonBase.determineOrientation(world, bx, by, bz, player)),
					SpaceDistortion.stargateRingShape, SpaceDistortion.templateBlockInfo, -6, -3).addToWorld(world);;
			*/
		}
		return true;
	}
}
