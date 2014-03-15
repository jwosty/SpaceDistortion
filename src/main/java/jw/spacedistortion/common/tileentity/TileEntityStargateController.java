package jw.spacedistortion.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

public class TileEntityStargateController extends TileEntity {
	/**
	 * Represents the coordinates the player has entered thus so far: the first
	 * 6 decimal places are the destination chunk's x coordinate, the next 6 are
	 * the destination chunk's z coordinate, and the last 2 places specify the
	 * dimension
	 */
	public byte[] dialingAddress;
	public int currentGlyphIndex;
	public StargateControllerState lastState;
	
	public TileEntityStargateController() {
		this.resetAddress();
	}
	
	public void resetAddress() {
		this.dialingAddress = new byte[] { 40, 40, 40, 40, 40, 40, 40 };
		this.currentGlyphIndex = 0;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		data.setInteger("state", this.lastState.value());
		data.setByteArray("address", this.dialingAddress);
		data.setInteger("glyph", this.currentGlyphIndex);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		this.lastState = StargateControllerState.values()[data.getInteger("state")];
		this.dialingAddress = data.getByteArray("address");
		this.currentGlyphIndex = data.getInteger("glyph");
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
