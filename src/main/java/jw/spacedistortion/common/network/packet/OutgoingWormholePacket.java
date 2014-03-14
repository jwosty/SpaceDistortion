package jw.spacedistortion.common.network.packet;

import io.netty.buffer.ByteBuf;
import jw.spacedistortion.Triplet;
import jw.spacedistortion.common.block.BlockStargateController;
import jw.spacedistortion.common.block.SDBlock;
import jw.spacedistortion.common.network.ChannelHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.relauncher.Side;

public class OutgoingWormholePacket implements IPacket {
	
	public int dhdX;
	public int dhdY;
	public int dhdZ;
	/* Destination chunk address */
	public byte[] address;
	
	public OutgoingWormholePacket(int dhdX, int dhdY, int dhdZ, byte[] address) {
		this.dhdX = dhdX;
		this.dhdY = dhdY;
		this.dhdZ = dhdZ;
		this.address = address;
	}

	public OutgoingWormholePacket() { }
	
	@Override
	public void readBytes(ByteBuf bytes) {
		dhdX = bytes.readInt();
		dhdY = bytes.readInt();
		dhdZ = bytes.readInt();
	}
	
	@Override
	public void writeBytes(ByteBuf bytes) {
		bytes.writeInt(dhdX);
		bytes.writeInt(dhdY);
		bytes.writeInt(dhdZ);
	}
}
