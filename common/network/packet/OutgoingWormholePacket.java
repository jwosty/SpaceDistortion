package jw.spacedistortion.common.network.packet;

import jw.spacedistortion.Tuple;
import jw.spacedistortion.common.block.BlockStargateController;
import jw.spacedistortion.common.block.SDBlock;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;

import cpw.mods.fml.relauncher.Side;

public class OutgoingWormholePacket extends SDPacket {
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

	public OutgoingWormholePacket() {
	}

	@Override
	public void write(ByteArrayDataOutput out) {
		out.writeInt(this.dhdX);
		out.writeInt(this.dhdY);
		out.writeInt(this.dhdZ);
		out.write(this.address);
	}

	@Override
	public void read(ByteArrayDataInput in) {
		this.address = new byte[7];
		this.dhdX = in.readInt();
		this.dhdY = in.readInt();
		this.dhdZ = in.readInt();
		in.readFully(this.address);
	}

	@Override
	public void excecute(EntityPlayer player, Side side)
			throws ProtocolException {
		Tuple<Integer, Tuple<Integer, Integer>> decodedAddress = SDBlock.stargateController
				.decodeAddress(address);
		int dimension = decodedAddress.X;
		int xDest = decodedAddress.Y.X;
		int zDest = decodedAddress.Y.Y;
		if (side.isServer()) {
			player.addChatMessage("dhdX = " + dhdX + ", dhdY = " + dhdY
					+ ", dhdZ = " + dhdZ + "\nxDest = " + xDest + ", zDest = "
					+ zDest + "\nside = " + side);
			World world = Minecraft.getMinecraft().theWorld;
			int[] coords = BlockStargateController.getDominantController(world,
					xDest, zDest);
			if (coords != null) {
				System.out.println("Locked onto destination Stargate");
				world.setBlock(coords[0], coords[1], coords[2],
						Block.blockLapis.blockID);
				//SDBlock.stargateController.serverActivateStargatePair(
				//		player.worldObj, dhdX, dhdY, dhdZ, coords[0],
				//		coords[1], coords[2]);
			} else {
				System.out.println("No destination Stargate detected");
			}
		} else {
			throw new ProtocolException(
					"Cannot send this packet to the client!");
		}
	}
}
