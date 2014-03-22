package jw.spacedistortion.common.network.packet;

import jw.spacedistortion.Triplet;
import jw.spacedistortion.common.block.BlockStargateController;
import jw.spacedistortion.common.block.SDBlock;
import jw.spacedistortion.common.tileentity.TileEntityStargateController;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import io.netty.buffer.ByteBuf;

/**
 * Connects or disconnects two stargates
 */
public class PacketWormhole implements IPacket {
	public int dhdX;
	public int dhdY;
	public int dhdZ;
	public byte[] address = new byte[7];
	public boolean createConnection;
	
	public PacketWormhole(int dhdX, int dhdY, int dhdZ, byte[] address, boolean createConnection) {
		this.dhdX = dhdX;
		this.dhdY = dhdY;
		this.dhdZ = dhdZ;
		this.address = address;
		this.createConnection = createConnection;
	}

	public PacketWormhole() { }
	
	@Override
	public void readBytes(ByteBuf bytes) {
		this.dhdX = bytes.readInt();
		this.dhdY = bytes.readInt();
		this.dhdZ = bytes.readInt();
		bytes.readBytes(this.address);
		this.createConnection = bytes.readBoolean();
	}
	
	@Override
	public void writeBytes(ByteBuf bytes) {
		bytes.writeInt(this.dhdX);
		bytes.writeInt(this.dhdY);
		bytes.writeInt(this.dhdZ);
		bytes.writeBytes(this.address);
		bytes.writeBoolean(this.createConnection);
	}
	
	@Override
	public void onReceive(EntityPlayer player, Side side) {
		Triplet<Integer, Integer, Integer> decodedAddress = SDBlock.stargateController.decodeAddress(address);
		int destDimension = decodedAddress.X;
		int destX = decodedAddress.Y;
		int destZ = decodedAddress.Z;
		if (side == Side.SERVER) {
			int[] coords = BlockStargateController.getDominantController(player.worldObj, destX, destZ);
			if (coords != null) {
				if (this.createConnection) {
					SDBlock.stargateController.serverActivateStargatePair(
							player.worldObj, dhdX, dhdY, dhdZ,
							coords[0], coords[1], coords[2]);
				} else {
					SDBlock.stargateController.serverDeactivateStargatePair(
							player.worldObj, dhdX, dhdY, dhdZ,
							coords[0], coords[1], coords[2]);
				}
			}
		}
	}
}
