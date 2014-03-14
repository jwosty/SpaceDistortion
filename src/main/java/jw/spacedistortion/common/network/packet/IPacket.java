package jw.spacedistortion.common.network.packet;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import io.netty.buffer.ByteBuf;

public interface IPacket {
	public void readBytes(ByteBuf bytes);
	public void writeBytes(ByteBuf bytes);
	public void onReceive(EntityPlayer player, Side side);
}