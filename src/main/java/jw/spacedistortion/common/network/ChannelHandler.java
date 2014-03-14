package jw.spacedistortion.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import java.util.EnumMap;

import jw.spacedistortion.common.network.packet.IPacket;
import jw.spacedistortion.common.network.packet.OutgoingWormholePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.FMLEmbeddedChannel;
import cpw.mods.fml.common.network.FMLIndexedMessageToMessageCodec;
import cpw.mods.fml.common.network.FMLOutboundHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ChannelHandler extends FMLIndexedMessageToMessageCodec<IPacket> {
	public ChannelHandler() {
		this.addDiscriminator(0, OutgoingWormholePacket.class);
	}

	public static EnumMap<Side, FMLEmbeddedChannel> channels;
	
	public static void initChannels() {
		channels = NetworkRegistry.INSTANCE.newChannel("SpaceDistortion", new ChannelHandler());
	}
	
	@SideOnly(Side.CLIENT)
	/**
	 * Sends a packet from the client to the server
	 * @param packet An IPacket to send
	 */
	public static void clientSendPacket(IPacket packet) {
		FMLEmbeddedChannel channel = ChannelHandler.channels.get(Side.CLIENT);
		channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.TOSERVER);
		channel.writeOutbound(packet);
	}
	
	@SideOnly(Side.SERVER)
	/**
	 * Sends a packet from the server to a given client
	 * @param packet An IPacket to send
	 * @param player A player to send it to
	 */
	public static void serverSentPacket(IPacket packet, EntityPlayer player) {
		FMLEmbeddedChannel channel = ChannelHandler.channels.get(Side.SERVER);
		channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.PLAYER);
		channel.attr(FMLOutboundHandler.FML_MESSAGETARGETARGS).set(player);
		channel.writeOutbound(packet);
	}
	
	/**
	 * Sends a packet from the server to all clients
	 * @param packet An IPacket to send
	 */
	@SideOnly(Side.SERVER)
	public static void serverSendPacketAllClients(IPacket packet) {
		FMLEmbeddedChannel channel = ChannelHandler.channels.get(Side.SERVER);
		channel.attr(FMLOutboundHandler.FML_MESSAGETARGET).set(FMLOutboundHandler.OutboundTarget.ALL);
		channel.writeOutbound(packet);
	}
	
	@Override
	public void encodeInto(ChannelHandlerContext context, IPacket packet, ByteBuf data) throws Exception {
		packet.writeBytes(data);
	}
	
	@Override
	public void decodeInto(ChannelHandlerContext context, ByteBuf data, IPacket packet) {
		// read the packet
		packet.readBytes(data);
		
		// handle the packet by calling IPacket#onReceive
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		EntityPlayer player;
		switch (side) {
			case CLIENT:
				player = Minecraft.getMinecraft().thePlayer;
				packet.onReceive(player, side);
			case SERVER:
				INetHandler net = context.channel().attr(NetworkRegistry.NET_HANDLER).get();
				player = ((NetHandlerPlayServer) net).playerEntity;
				packet.onReceive(player, side);
		}
	}
}
