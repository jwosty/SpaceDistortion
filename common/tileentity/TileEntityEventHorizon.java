package jw.spacedistortion.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityEventHorizon extends TileEntity {
	// Coordinates of the destination stargate controller
	public int xDest;
	public int yDest;
	public int zDest;
	
	public TileEntityEventHorizon(int xDest, int yDest, int zDest) {
		this.xDest = xDest;
		this.yDest = yDest;
		this.zDest = zDest;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		data.setIntArray("destCoords", new int[]{xDest, yDest, zDest});
		System.out.println("x = " + this.xDest + ", y = " + this.yDest + ", z = " + this.zDest);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		int[] destCoords = data.getIntArray("destCoords");
		xDest = destCoords[0];
		yDest = destCoords[1];
		zDest = destCoords[2];
		System.out.println("x = " + this.xDest + ", y = " + this.yDest + ", z = " + this.zDest);
	}
}