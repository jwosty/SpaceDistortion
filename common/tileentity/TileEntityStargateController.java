package jw.spacedistortion.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityStargateController extends TileEntity {
	public boolean isActivated = false;
	public boolean isOutgoing = false;
	// Coordinates of the destination stargate controller
	public int xDest = 0;
	public int yDest = 0;
	public int zDest = 0;
	
	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.readFromNBT(data);
	}
	
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
	}
}