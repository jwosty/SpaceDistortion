package jw.spacedistortion.common.network;

import net.minecraft.entity.player.EntityPlayer;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

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
	
	public abstract void write(ByteArrayDataOutput out);
	
	public abstract void read(ByteArrayDataInput in);
	
	public abstract void excecute(EntityPlayer player, Side side);
}
