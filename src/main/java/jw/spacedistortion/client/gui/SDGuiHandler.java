package jw.spacedistortion.client.gui;

import jw.spacedistortion.common.SpaceDistortion;
import jw.spacedistortion.common.block.SDBlock;
import jw.spacedistortion.common.block.Structure;
import jw.spacedistortion.common.tileentity.TileEntityStargateController;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.network.IGuiHandler;

public class SDGuiHandler implements IGuiHandler {
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		Block block = world.getBlock(x, y, z);
		try {
			switch (ID) {
			case 0:
				TileEntity tileEntity = world.getTileEntity(x, y, z);
				return new GuiDHD((TileEntityStargateController)tileEntity);
			case 1:
				Structure r = Structure.detectStructure(world, x, y, z, SpaceDistortion.transporterRingsShape, SpaceDistortion.templateBlockInfo, ForgeDirection.UP);
				return new GuiRingPlatform(r.x, r.y, r.z);
			}
		} catch (NullPointerException e) {
			return null;
		}
		return null;
	}

}
