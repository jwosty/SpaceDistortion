package jw.spacedistortion.common.tileentity;

import jw.spacedistortion.Axis;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityEventHorizon extends TileEntity {
	public boolean isOutgoing = false;
	public Axis axis;
	// Coordinates of the destination stargate controller
	public int destX;
	public int destY;
	public int destZ;

	public TileEntityEventHorizon() { }
	
	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		data.setBoolean("outgoing", isOutgoing);
		if (axis != null) {
			data.setInteger("axis", this.axis.toInt());
		}
		data.setIntArray("destCoords", new int[]{this.destX, this.destY, this.destZ});
	}
	
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		this.isOutgoing = data.getBoolean("outgoing");
		Integer a = data.getInteger("axis");
		if (a != null) {
			this.axis = Axis.ofInt(a);
		}
		int[] destCoords = data.getIntArray("destCoords");
		destX = destCoords[0];
		destY = destCoords[1];
		destZ = destCoords[2];
	}
}