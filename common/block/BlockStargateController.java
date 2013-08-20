package jw.spacedistortion.common.block;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.List;

import jw.spacedistortion.StringGrid;
import jw.spacedistortion.client.gui.GuiDHD;
import jw.spacedistortion.common.CommonProxy;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockStargateController extends SDBlock {
	public static StringGrid stargateRingShape = new StringGrid(
			"  XXX  ",
			" X   X ",
			"X     X",
			"X     X",
			"X     X",
			" X   X ",
			"  XXX  ");
	public static StringGrid stargateEventHorizonShape = new StringGrid(
			"       ",
			"  XXX  ",
			" XXXXX ",
			" XXXXX ",
			" XXXXX ",
			"  XXX  ",
			"       ");

	// The coordinate at which the textures for this block starts
	private int blockIndexInTexture;
	private int textureTop = 2;

	public BlockStargateController(int id, int _blockIndexInTexture) {
		super(id, Material.rock);
		blockIndexInTexture = _blockIndexInTexture;
	}

	// Returns the coordinates of the dominate (first found) stargate controller
	// in the given chunk; null if none is found
	public static int[] getDominantController(World world, int chunkX,
			int chunkZ) {
		System.out.println("Searching for Stargate at chunk (" + chunkX + ", "
				+ chunkZ + ")");
		Chunk chunk = world.getChunkFromChunkCoords(chunkX, chunkZ);
		for (int x = 0; x < 16; x++) {
			for (int z = 0; z < 16; z++) {
				for (int y = 0; y < 256; y++) {
					int block = chunk.getBlockID(x, y, z);
					int rx = (chunkX << 4) + x;
					int rz = (chunkZ << 4) + z;
					// }
					if (block == SDBlock.stargateController.blockID) {
						return new int[] { rx, y, rz };
					}
				}
			}
		}
		return null;
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int par1, float par2, float par3, float par4) {
		// int[] coords = this.getDominantController(world, x >> 4, z >> 4);
		// if (coords != null) {
		// System.out.println("Found a stargate at (" + coords[0] + ", " +
		// coords[1] + ", " + coords[2] + ")");
		// player.setPosition(coords[0] + 0.5, coords[1] + 1.0, coords[2] +
		// 0.5);
		// }
		if (world.isRemote) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiDHD(x, y, z));
		}
		return true;
	}

	@SideOnly(Side.CLIENT)
	public void addressReceived(byte[] address, int dhdX, int dhdY, int dhdZ) {
		for (int i = 0; i < 7; i++) {
			if (i < 6) {
				System.out.print(address[i] + " ");
			} else {
				System.out.print("(" + Integer.toBinaryString(address[i])
						+ ")\n");
			}
		}
		// Building base 39 numbers using powers of 3
		int chunkX = (int) ((address[0] * 1521) + (address[1] * 39) + address[2]);
		int chunkZ = (int) ((address[3] * 1521) + (address[4] * 39) + address[2]);
		int last = (int) address[6];
		// The dimension is stored in the last 2 bits of the last number/symbol
		// (the
		// mask is 0b11)
		int dimension = last & 3;
		// The sign of the x coordinate is the 4rd to last bit (the mask is
		// 0b1000)
		int xSign = last & 8;
		// The sign of the z coordinate is the 3th to last bit (the mask is
		// 0b100)
		int zSign = last & 4;
		if (xSign == 0) {
			chunkX = -chunkX;
		}
		if (zSign == 0) {
			chunkZ = -chunkZ;
		}
		System.out.println("chunkX = " + chunkX + ", chunkZ = " + chunkZ
				+ ", dimension = " + dimension);
		
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream outputStream = new DataOutputStream(bos);
		try {
			// X, y, and z coordinates
			outputStream.writeInt(dhdX);
			outputStream.writeInt(dhdY);
			outputStream.writeInt(dhdZ);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		// Copy the data to the packet
		Packet250CustomPayload packet = new Packet250CustomPayload();
		packet.channel = "OutgoingWormhole";
		packet.data = bos.toByteArray();
		packet.length = bos.size();
		
		// Send the data over the wire
		PacketDispatcher.sendPacketToServer(packet);
		
		//this.activateStargate(dhdX, dhdY, dhdZ);
		
		// int[] controllerCoords = this.getDominantController(
		// Minecraft.getMinecraft().theWorld, chunkX, chunkZ);
		// if (controllerCoords == null) {
		// Minecraft.getMinecraft().thePlayer
		// .sendChatToPlayer("Cheveron 7 will not lock!");
		// } else {
		// Minecraft.getMinecraft().thePlayer
		// .sendChatToPlayer("Cheveron 7 locked! Target stargate located at ("
		// + controllerCoords[0]
		// + ", "
		// + controllerCoords[1]
		// + ", " + controllerCoords[2] + ")");
		// Minecraft.getMinecraft().thePlayer.setPositionAndUpdate(
		// controllerCoords[0], controllerCoords[1] + 1,
		// controllerCoords[2]);
		// }
	}
	
	/**
	 * Ask the server to activate the stargate attatched to the controller at
	 * the given coordinates
	 */
	@SideOnly(Side.CLIENT)
	public void clientActivateStargate(int x, int y, int z) {
		
	}
	
	/**
	 * Activate the stargate attached to the given controller coordinates 
	 */
	@SideOnly(Side.SERVER)
	public void serverActivateStargate(int x, int y, int z) {
		World world = Minecraft.getMinecraft().theWorld;
		// If there's no controller block, don't continue
		if (world.getBlockId(x, y, z) != this.blockID) {
			System.out.println("No controller");
			return;
		}
		DetectStructureResults stargate = this.getStargateBlocks(Minecraft.getMinecraft().theWorld, x, y, z);
		if (stargate == null) {
			System.out.println("No stargate");
			return;
		}
		Integer[] firstNeighbor = this.getNeighboringBlocks(world, x, y, z).get(0);
		// This is the top-left corner of the stargate ring
		int[] origin = this.getBlockInStructure(world, firstNeighbor[0], firstNeighbor[1], firstNeighbor[2], -stargate.xOffset, stargate.yOffset, stargate.plane);
		// Fill the center of the ring with EventHorizon blocks
		for (int templateX = 0; templateX <= stargateEventHorizonShape.width; templateX++) {
			for (int templateY = 0; templateY <= stargateEventHorizonShape.height; templateY++) {
				if (stargateEventHorizonShape.get(templateX, templateY) == 'X') {
					int[] coords = this.getBlockInStructure(world, origin[0],
							origin[1], origin[2], templateX, -templateY,
							stargate.plane);
					//((EntityClientPlayerMP) Minecraft.getMinecraft().thePlayer).sendQueue
					//		.addToSendQueue(new Packet15Place(coords[0],
					//				coords[1], coords[2], 0, new ItemStack(Block.stone, 64), 0, 0, 0));
					world.setBlockWithNotify(coords[0], coords[1], coords[2], SDBlock.stone.blockID);
					// world.setBlockTileEntity(coords[0], coords[1], coords[2], new TileEntityEventHorizon(templateX, templateY));
				}
			}
		}
	}

	/**
	 * Returns the position of the first neighboring block found that is a
	 * stargate ring Coordinates in returns are not relative to the given
	 * coordinates Does nothing yet
	 **/
	public DetectStructureResults getStargateBlocks(World world, int xOrigin,
			int yOrigin, int zOrigin) {
		List<Integer[]> neighbors = this.getNeighboringBlocks(world, xOrigin, yOrigin, zOrigin);
		for (int i = 0; i < neighbors.size(); i++) {
			Integer[] blockInfo = neighbors.get(i);
			if (blockInfo[3] == SDBlock.stargateRing.blockID) {
				DetectStructureResults results = SDBlock.detectStructure(world,
						stargateRingShape, blockInfo[0], blockInfo[1],
						blockInfo[2], SDBlock.stargateRing.blockID);
				if (results != null) {
					return results;
				}
			}
		}
		return null;
	}

	@Override
	public int getBlockTextureFromSide(int side) {
		int offset;
		if (side == 1) {
			offset = 1;
		} else {
			offset = 0;
		}
		return blockIndexInTexture + offset;
	}

	@Override
	public String getTextureFile() {
		return CommonProxy.TEXTURES_PNG;
	}
}
