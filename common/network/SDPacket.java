package jw.spacedistortion.common.network;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;

public abstract class SDPacket {
	public static final String CHANNEL = "OutgoingWormhole";
	private static final BiMap<Integer, Class<? extends SDPacket>> idMap;
	
	static {
		ImmutableBiMap.Builder<Integer, Class<? extends SDPacket>> builder = ImmutableBiMap.builder();
		
		idMap = builder.build();
	}
	
	public static SDPacket constructPacket(int packetID) throws ProtocolException, InstantiationException, IllegalAccessException {
		Class<? extends SDPacket> clazz = idMap.get(Integer.valueOf(packetID));
		if (clazz == null) {
			throw new ProtocolException("Unknown packet ID!");
		} else {
			return clazz.newInstance();
		}
	}
	
	public static class ProtocolException extends Exception {
		public ProtocolException() {}
		
		public ProtocolException(String message, Throwable cause) {
			super(message, cause);
		}
		
		public ProtocolException(String message) {
			super(message);
		}
		
		public ProtocolException(Throwable cause) {
			super(cause);
		}
	}
	
	public final int getPacketID() {
		if (idMap.inverse().containsKey(this.getClass())) {
			return idMap.inverse().get(this.getClass()).intValue();
		} else {
			throw new RuntimeException("Packet " + this.getClass().getSimpleName() + " is missing a mapping!");
		}
	}
	
	public final Packet makePacket() {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeByte(this.getPacketID());
		write(out);
		return PacketDispatcher.getPacket(CHANNEL, out.toByteArray());
	}
	
	public abstract void write(ByteArrayDataOutput out);
	
	public abstract void read(ByteArrayDataInput in);
	
	public abstract void excecute(EntityPlayer player, Side side);
}
