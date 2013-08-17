package jw.spacedistortion.common.block;

import java.util.List;

import jw.spacedistortion.StringGrid;
import jw.spacedistortion.client.gui.GuiDHD;
import jw.spacedistortion.common.CommonProxy;
import jw.spacedistortion.common.tileentity.TileEntityStargateController;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class BlockStargateController extends Block {
	public static StringGrid stargateRingShape = new StringGrid("  XXX  ",
			" X   X ", "X     X", "X     X", "X     X", " X   X ", "  XXX  ");
	public static StringGrid stargateEventHorizonShape = new StringGrid(
			"       ", "  XXX  ", " XXXXX ", " XXXXX ", " XXXXX ", "  XXX  ",
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
		
		this.activateStargate(dhdX, dhdY, dhdZ);
		
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
	 * Activate the stargate attached to the given controller coordinates (does
	 * nothing if the controller doesn't exist or isn't adjacent to a stargate)
	 */
	public void activateStargate(int x, int y, int z) {
		
	}

	/**
	 * Returns the position of the first neighboring block found that is a
	 * stargate ring Coordinates in returns are not relative to the given
	 * coordinates Does nothing yet
	 **/
	public DetectStructureResults getStargateBlocks(World world, int xOrigin,
			int yOrigin, int zOrigin) {
		// Hmm, a bit of an odd workaround... Make that method static? :/
		List<Integer[]> neighbors = ((SDBlock) SDBlock.stargateRing)
				.getNeighboringBlocks(world, xOrigin, yOrigin, zOrigin);
		for (int i = 0; i < neighbors.size(); i++) {
			Integer[] blockInfo = neighbors.get(i);
			if (blockInfo[3] == SDBlock.stargateRing.blockID) {
				DetectStructureResults results = ((SDBlock) SDBlock.stargateRing)
						.detectStructure(world, stargateRingShape,
								blockInfo[0], blockInfo[1], blockInfo[2]);
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
