package jw.spacedistortion.common.tileentity;

import java.util.ArrayList;

import jw.spacedistortion.common.block.SDBlock;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityStargateController extends TileEntity {
	public StargateControllerStateOld state;
	/** Stores the coordinates the player has entered thus so far */
	public byte[] addressBuffer;
	public int currentGlyphIndex;
	
	public int connectedXCoord;
	public int connectedYCoord;
	public int connectedZCoord;
	public int connectedDimension;
	
	public TileEntityStargateController() {
		// It actually doesn't matter what state we use; BlockStargateController#onBlockPlacedBy or
		// TileEntityStargateController#readFromNBT will correct this
		this.reset(StargateControllerStateOld.NO_CONNECTED_STARGATE);
	}
	
	/**
	 * Resets a stargate to a new state, blanking out everything else
	 * @param newState The new state to initialize with -- if null, uses SDBlock.stargateController.getCurrentState
	 * to determine the new state
	 */
	public void reset(StargateControllerStateOld newState) {
		if (newState == null) {
			this.state = SDBlock.stargateController.getCurrentState(this.worldObj, this.xCoord, this.yCoord, this.zCoord);
		} else {
			this.state = newState;
		}
		this.addressBuffer = new byte[] { 40, 40, 40, 40, 40, 40, 40 };
		this.currentGlyphIndex = 0;
		this.connectedXCoord = 0;
		this.connectedYCoord = 0;
		this.connectedZCoord = 0;
		this.connectedDimension = 0;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		data.setInteger("state", this.state.value());
		switch (this.state) {
		case NO_CONNECTED_STARGATE:
		case READY:
			data.setByteArray("address", this.addressBuffer);
			data.setInteger("glyph", this.currentGlyphIndex);
			break;
		case ACTIVE_OUTGOING:
		case ACTIVE_INCOMING:
			data.setInteger("connx", this.connectedXCoord);
			data.setInteger("conny", this.connectedYCoord);
			data.setInteger("connz", this.connectedZCoord);
			data.setInteger("connd", this.connectedDimension);
			break;
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		this.state = StargateControllerStateOld.values()[data.getInteger("state")];
		switch (this.state) {
		case NO_CONNECTED_STARGATE:
		case READY:
			this.addressBuffer = data.getByteArray("address");
			this.currentGlyphIndex = data.getInteger("glyph");			
			break;
		case ACTIVE_OUTGOING:
		case ACTIVE_INCOMING:
			this.connectedXCoord = data.getInteger("connx");
			this.connectedYCoord = data.getInteger("conny");
			this.connectedZCoord = data.getInteger("connz");
			this.connectedDimension = data.getInteger("connd");
			break;
		}
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
