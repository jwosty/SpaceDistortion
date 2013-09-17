package jw.spacedistortion.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;

public class TileEntityStargateController extends TileEntityEventHorizon {
	public boolean isActivated = false;
	public boolean isOutgoing = false;
	
	public TileEntityStargateController(boolean isActivated, boolean isOutgoing, int xDest, int yDest, int zDest) {
		super(xDest, yDest, zDest);
		this.isActivated = isActivated;
		this.isOutgoing = isOutgoing;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		data.setBoolean("isActivated", isActivated);
		data.setBoolean("isOutgoing", isOutgoing);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		this.isActivated = data.getBoolean("isActivated");
		this.isOutgoing = data.getBoolean("isOutgoing");
	}
}