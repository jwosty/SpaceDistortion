package jw.spacedistortion.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityEventHorizon extends TileEntity {
	public boolean isOutgoing = false;
	public int plane;
	// Coordinates of the destination stargate controller
	public int destX;
	public int destY;
	public int destZ;

	public TileEntityEventHorizon() { }
	
	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		data.setBoolean("outgoing", isOutgoing);
		data.setInteger("plane", this.plane);
		data.setIntArray("destCoords", new int[]{this.destX, this.destY, this.destZ});
	}
	
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		this.isOutgoing = data.getBoolean("outgoing");
		this.plane = data.getInteger("plane");
		int[] destCoords = data.getIntArray("destCoords");
		destX = destCoords[0];
		destY = destCoords[1];
		destZ = destCoords[2];
	}
}