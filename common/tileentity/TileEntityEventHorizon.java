package jw.spacedistortion.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityEventHorizon extends TileEntity {
	// Coordinates of the destination stargate controller
	public int destX;
	public int destY;
	public int destZ;
	
	public TileEntityEventHorizon(int xDest, int yDest, int zDest) {
		this.destX = xDest;
		this.destY = yDest;
		this.destZ = zDest;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		data.setIntArray("destCoords", new int[]{destX, destY, destZ});
		System.out.println("TileEntityEventHorizon write x = " + this.destX + ", y = " + this.destY + ", z = " + this.destZ);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		int[] destCoords = data.getIntArray("destCoords");
		destX = destCoords[0];
		destY = destCoords[1];
		destZ = destCoords[2];
		System.out.println("TileEntityEventHorizon read x = " + this.destX + ", y = " + this.destY + ", z = " + this.destZ);
	}
}