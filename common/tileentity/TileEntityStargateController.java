package jw.spacedistortion.common.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;

public class TileEntityStargateController extends TileEntity {
	public StargateControllerState state;
	
	public TileEntityStargateController() {
		System.out.println("TileEntityStargateController constructor called");
	}
	
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
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		this.writeToNBT(tag);
		return new Packet132TileEntityData(this.xCoord, this.yCoord, this.zCoord, 1, tag);
	}
	
	@Override
	public void onDataPacket(INetworkManager net, Packet132TileEntityData data) {
		this.readFromNBT(data.customParam1);
	}
}
