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
		data.setBoolean("isActivated", isActivated);
		data.setBoolean("isOutgoing", isOutgoing);
		data.setIntArray("destCoords", new int[]{xDest, yDest, zDest});
	}
	
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		this.isActivated = data.getBoolean("isActivated");
		this.isOutgoing = data.getBoolean("isOutgoing");
		int[] destCoords = data.getIntArray("destCoords");
		xDest = destCoords[0];
		yDest = destCoords[1];
		yDest = destCoords[2];
	}
}