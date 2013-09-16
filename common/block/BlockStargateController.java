package jw.spacedistortion.common.block;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.List;

import jw.spacedistortion.StringGrid;
import jw.spacedistortion.client.gui.GuiDHD;
import jw.spacedistortion.common.network.packet.OutgoingWormholePacket;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.Icon;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import cpw.mods.fml.common.FMLCommonHandler;
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
	
	private Icon controllerTopIcon;
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IconRegister register) {
		super.registerIcons(register);
		controllerTopIcon = register.registerIcon(this.getIconName() + "_top");
	}
	
	public BlockStargateController(int id) {
		super(id, Material.rock);
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
		if (!world.isRemote) {
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
		
		Side side = Minecraft.getMinecraft().theWorld.isRemote ? Side.CLIENT : Side.SERVER;//Side side = FMLCommonHandler.instance().getSide();
		
		// Send the data over the wire
		Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new OutgoingWormholePacket(dhdX, dhdY, dhdZ).makePacket());
	}
	
	/**
	 * Activate the stargate attached to the given controller coordinates 
	 */
	//@SideOnly(Side.SERVER)
	public void serverActivateStargate(World world, int x, int y, int z) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
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
					world.setBlock(coords[0], coords[1], coords[2], SDBlock.eventHorizon.blockID, 0, 2);
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
	public Icon getIcon(int side, int metadata) {
		return side == 1 ? this.controllerTopIcon : this.blockIcon;
	}
}
