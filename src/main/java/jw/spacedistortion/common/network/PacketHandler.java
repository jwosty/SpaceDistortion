package jw.spacedistortion.common.network;

import jw.spacedistortion.common.network.packet.SDPacket;
import jw.spacedistortion.common.network.packet.SDPacket.ProtocolException;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;

import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PacketHandler implements IPacketHandler {
	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		Side side = Minecraft.getMinecraft().theWorld.isRemote ? Side.CLIENT : Side.SERVER;
		try {
			EntityPlayer entityPlayer = (EntityPlayer)player;
			ByteArrayDataInput in = ByteStreams.newDataInput(packet.data);
			int packetID = in.readUnsignedByte();
			SDPacket sdPacket = SDPacket.constructPacket(packetID);
			sdPacket.read(in);
			sdPacket.excecute(entityPlayer, entityPlayer.worldObj.isRemote ? Side.CLIENT : Side.SERVER);
		} catch (ProtocolException e) {
			if (player instanceof EntityPlayerMP) {
				((EntityPlayerMP) player).playerNetServerHandler.kickPlayerFromServer("Protocol Exception!");
			}
		} catch (Exception e) {
			throw new RuntimeException("Unexpected Reflection exception during Packet construction!", e);
		}
	}
}
