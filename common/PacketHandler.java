package jw.spacedistortion.common;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

import jw.spacedistortion.common.block.SDBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet250CustomPayload;
import cpw.mods.fml.common.network.IPacketHandler;
import cpw.mods.fml.common.network.Player;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class PacketHandler implements IPacketHandler {
	@Override
	public void onPacketData(INetworkManager manager,
			Packet250CustomPayload packet, Player player) {
		Side side = Minecraft.getMinecraft().theWorld.isRemote ? Side.CLIENT : Side.SERVER;//FMLCommonHandler.instance().getSide();
		if (packet.channel.equals("OutgoingWormhole")) {
			this.handleOutgoingWormhole(packet);
		}
	}
	
	@SideOnly(Side.SERVER)
	/**
	 * Handles an outgoingWormhole packet
	 * @param x The x coordinate of the DHD dialed from
	 * @param y The y coordinate of the DHD dialed from
	 * @param z The z coordinate of the DHD dialed from
	 */
	public void handleOutgoingWormhole(Packet250CustomPayload packet) {
		DataInputStream inputStream = new DataInputStream(new ByteArrayInputStream(packet.data));
		int x;
		int y;
		int z;
		try {
			x = inputStream.readInt();
			y = inputStream.readInt();
			z = inputStream.readInt();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		SDBlock.stargateController.serverActivateStargate(x, y, z);
	}
}
