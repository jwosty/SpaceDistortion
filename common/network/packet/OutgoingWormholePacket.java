package jw.spacedistortion.common.network.packet;

import jw.spacedistortion.common.block.SDBlock;
import net.minecraft.entity.player.EntityPlayer;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.relauncher.Side;

public class OutgoingWormholePacket extends SDPacket {
	public int dhdX;
	public int dhdY;
	public int dhdZ;
	/* Destination chunk x */
	public int xDest;
	/* Destination chunk z */
	public int zDest;

	public OutgoingWormholePacket(int dhdX, int dhdY, int dhdZ, int xDest,
			int zDest) {
		this.dhdX = dhdX;
		this.dhdY = dhdY;
		this.dhdZ = dhdZ;
		this.xDest = xDest;
		this.zDest = zDest;
	}

	public OutgoingWormholePacket() {
	}

	@Override
	public void write(ByteArrayDataOutput out) {
		out.writeInt(this.dhdX);
		out.writeInt(this.dhdY);
		out.writeInt(this.dhdZ);
		out.writeInt(this.xDest);
		out.writeInt(this.zDest);
	}

	@Override
	public void read(ByteArrayDataInput in) {
		this.dhdZ = in.readInt();
		this.dhdY = in.readInt();
		this.dhdX = in.readInt();
		this.xDest = in.readInt();
		this.zDest = in.readInt();
	}

	@Override
	public void excecute(EntityPlayer player, Side side)
			throws ProtocolException {
		if (side.isServer()) {
			player.addChatMessage("dhdX = " + dhdX + ", dhdY = " + dhdY
					+ ", dhdZ = " + dhdZ + "\nxDest = " + xDest + ", zDest = "
					+ zDest + "\nside = " + side);
			// SDBlock.stargateController.serverActivateStargate(player.worldObj,
			// dhdX, dhdY, dhdZ, xDest, zDest);
		} else {
			throw new ProtocolException(
					"Cannot send this packet to the client!");
		}
	}
}
