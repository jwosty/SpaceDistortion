package jw.spacedistortion.common.network.packet;

import cpw.mods.fml.relauncher.Side;
import net.minecraft.entity.player.EntityPlayer;
import io.netty.buffer.ByteBuf;

/** Implement this interface to add more packets. VERY IMPORTANT: Do not forget to register
 * new packet classes in ChannelHandler() */
public interface IPacket {
	public void readBytes(ByteBuf bytes);
	public void writeBytes(ByteBuf bytes);
	public void onReceive(EntityPlayer player, Side side);
}