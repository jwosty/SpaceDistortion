package jw.spacedistortion.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileEntityStargateController extends TileEntity {
	public StargateControllerState state;
	
	public TileEntityStargateController() { }
	
	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		data.setInteger("state", this.state.value());
	}
	
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		this.state = StargateControllerState.values()[data.getInteger("state")];
	}
}
