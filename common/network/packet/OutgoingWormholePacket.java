package jw.spacedistortion.common.network.packet;

import net.minecraft.entity.player.EntityPlayer;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.relauncher.Side;

public class OutgoingWormholePacket extends SDPacket {
	public int dhdX;
	public int dhdY;
	public int dhdZ;
	
	public OutgoingWormholePacket(int dhdX, int dhdY, int dhdZ) {
		this.dhdX = dhdX;
		this.dhdY = dhdY;
		this.dhdZ = dhdZ;
	}
	
	public OutgoingWormholePacket() {}
	
	@Override
	public void write(ByteArrayDataOutput out) {
		out.writeInt(this.dhdX);
		out.writeInt(this.dhdY);
		out.writeInt(this.dhdZ);
	}

	@Override
	public void read(ByteArrayDataInput in) {
		this.dhdX = in.readInt();
		this.dhdY = in.readInt();
		this.dhdZ = in.readInt();
	}

	@Override
	public void excecute(EntityPlayer player, Side side) throws ProtocolException {
			if (side.isClient()) {
			player.addChatMessage("dhdX = " + dhdX + ", dhdY = " + dhdY + ", dhdZ = " + dhdZ);
		} else {
			throw new ProtocolException("Cannot send this packet to the client!");
		}
	}

}
