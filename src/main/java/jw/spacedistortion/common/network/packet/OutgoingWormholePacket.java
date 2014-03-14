package jw.spacedistortion.common.network.packet;

import jw.spacedistortion.Triplet;
import jw.spacedistortion.common.block.BlockStargateController;
import jw.spacedistortion.common.block.SDBlock;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import io.netty.buffer.ByteBuf;

public class OutgoingWormholePacket implements IPacket {
	
	public int dhdX;
	public int dhdY;
	public int dhdZ;
	/* Destination chunk address */
	public byte[] address = new byte[7];
	
	public OutgoingWormholePacket(int dhdX, int dhdY, int dhdZ, byte[] address) {
		this.dhdX = dhdX;
		this.dhdY = dhdY;
		this.dhdZ = dhdZ;
		this.address = address;
	}

	public OutgoingWormholePacket() { }
	
	@Override
	public void readBytes(ByteBuf bytes) {
		this.dhdX = bytes.readInt();
		this.dhdY = bytes.readInt();
		this.dhdZ = bytes.readInt();
		bytes.readBytes(this.address);
	}
	
	@Override
	public void writeBytes(ByteBuf bytes) {
		bytes.writeInt(this.dhdX);
		bytes.writeInt(this.dhdY);
		bytes.writeInt(this.dhdZ);
		bytes.writeBytes(this.address);
	}
	
	@Override
	public void onReceive(EntityPlayer player, Side side) {
		Triplet<Integer, Integer, Integer> decodedAddress = SDBlock.stargateController.decodeAddress(this.address);
		int destDimension = decodedAddress.X;
		int destX = decodedAddress.Y;
		int destZ = decodedAddress.Z;
		if (side == Side.SERVER) {
			player.addChatMessage(new ChatComponentText("dhdX = " + dhdX + ", dhdY = " + dhdY + ", dhdZ = " + dhdZ));
			player.addChatMessage(new ChatComponentText("destX = " + destX + ", destZ = " + destZ + ", destDimension = " + destDimension));
			player.addChatMessage(new ChatComponentText("side = " + side));
			World world = Minecraft.getMinecraft().theWorld;
			int[] coords = BlockStargateController.getDominantController(world, destX, destZ);
			if (coords != null) {
				System.out.println("Locked onto destination Stargate");
				SDBlock.stargateController.serverActivateStargatePair(
				player.worldObj, dhdX, dhdY, dhdZ, coords[0],
				coords[1], coords[2]);
			} else {
				System.out.println("No destination Stargate detected");
			}
		}
	}
}
