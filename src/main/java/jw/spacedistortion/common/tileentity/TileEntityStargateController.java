package jw.spacedistortion.common.tileentity;

import java.util.ArrayList;

import jw.spacedistortion.common.block.SDBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityStargateController extends TileEntity {
	public StargateControllerState state;
	
	public TileEntityStargateController() {
		// It actually doesn't matter what state we use; BlockStargateController#onBlockPlacedBy or
		// TileEntityStargateController#readFromNBT will correct this
		this.state = new StargateControllerState.StargateControllerInvalid();
	}
	
	/** Detects then sets the stargate's state */
	public void reset() {
		this.state = SDBlock.stargateController.getCurrentState(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		NBTTagCompound stateTag = new NBTTagCompound();
		this.state.writeToNBT(stateTag);
		data.setTag("state", stateTag);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		this.state = StargateControllerState.createFromNBT((NBTTagCompound) data.getTag("state"));
	}
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		this.writeToNBT(tag);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, tag);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity data) {
		this.readFromNBT(data.func_148857_g());
	}
}
