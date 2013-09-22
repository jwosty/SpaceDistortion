package jw.spacedistortion.common.block;

import java.util.List;

import jw.spacedistortion.StringGrid;
import jw.spacedistortion.Triplet;
import jw.spacedistortion.client.gui.GuiDHD;
import jw.spacedistortion.common.network.packet.OutgoingWormholePacket;
import jw.spacedistortion.common.tileentity.TileEntityEventHorizon;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IconRegister;
import net.minecraft.entity.player.EntityPlayer;
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

	public Triplet<Integer, Integer, Integer> decodeAddress(byte[] address) {
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
		int chunkZ = (int) ((address[3] * 1521) + (address[4] * 39) + address[5]);
		int last = (int) address[6];
		// The dimension is stored in the last 2 bits of the last
		// number/symbol
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
		return new Triplet(dimension, chunkX, chunkZ);
	}

	@SideOnly(Side.CLIENT)
	public void addressReceived(byte[] address, int dhdX, int dhdY, int dhdZ) {
		// Send the data over the wire
		Minecraft.getMinecraft().thePlayer.sendQueue
				.addToSendQueue(new OutgoingWormholePacket(dhdX, dhdY, dhdZ,
						address).makePacket());
	}
	
	/**
	 * Activate the stargate attached to the given controller coordinates
	 */
	public void serverActivateStargatePair(World world, int srcX, int srcY, int srcZ,
			int targetX, int targetY, int targetZ) {
		Side side = FMLCommonHandler.instance().getEffectiveSide();
		// If there's no controller block, don't continue
		if (world.getBlockId(srcX, srcY, srcZ) != SDBlock.stargateController.blockID) {
			System.out.println("No controller at (" + srcX + ", " + srcY + ", " + srcZ
					+ ")");
			return;
		}
		// Fill the dialing stargate
		this.fillStargateCenter(world, srcX, srcY, srcZ, targetX, targetY, targetZ);
		// Fill the destination stargate
		this.fillStargateCenter(world, targetX, targetY, targetZ, null, null, null);
	}

	/**
	 * Fills a given location w/event horizon blocks, assuming there's a
	 * stargate there
	 * 
	 * @param world
	 *            The world
	 * @param x
	 *            The x position of the stargate controller
	 * @param y
	 *            The y position of the stargate controller
	 * @param z
	 *            The z position of the stargate controller
	 * @param destX
	 *            The x position of the destination stargate (if any)
	 * @param destY
	 *            The y position of the destination stargate (if any)
	 * @param destZ
	 *            The z position of the destination stargate (if any)
	 */
	private void fillStargateCenter(World world, int x, int y, int z,
			Integer destX, Integer destY, Integer destZ) {
		// See if there's really a stargate here
		DetectStructureResults stargate = this.getStargateBlocks(
				Minecraft.getMinecraft().theWorld, x, y, z);
		// If there's not, log it
		if (stargate == null) {
			System.out.println("No stargate at (" + x + ", " + y + ", " + z
					+ ")");
			return;
		}
		
		Integer[] firstNeighbor = this.getNeighboringBlocks(world, x, y, z)
				.get(0);
		Triplet<Integer, Integer, Integer> origin = this
				.templateToWorldCoordinates(-stargate.xOffset, stargate.yOffset,
						stargate.plane);
		// Fill the center of the ring with EventHorizon blocks
		for (int templateX = 0; templateX <= stargateEventHorizonShape.width; templateX++) {
			for (int templateY = 0; templateY <= stargateEventHorizonShape.height; templateY++) {
				if (stargateEventHorizonShape.get(templateX, templateY) == 'X') {
				System.out.println("origin.X = " + origin.X + ", origin.Y = " + origin.Y + ", origin.Z = " + origin.Z);
					int[] coords = this.getBlockInStructure(world, origin.X + firstNeighbor[0],
							origin.Y + firstNeighbor[1], origin.Z + firstNeighbor[2], templateX, -templateY,
							stargate.plane);
					world.setBlock(coords[0], coords[1], coords[2],
							SDBlock.eventHorizon.blockID, 0, 2);
					if (destX != null && destY != null && destZ != null) {
						TileEntityEventHorizon tileEntity = (TileEntityEventHorizon) world
								.getBlockTileEntity(coords[0], coords[1],
										coords[2]);
						if (tileEntity != null) {
							tileEntity.isOutgoing = true;
							tileEntity.destX = destX;
							tileEntity.destY = destY;
							tileEntity.destZ = destZ;
						}
					}
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
		List<Integer[]> neighbors = this.getNeighboringBlocks(world, xOrigin,
				yOrigin, zOrigin);
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
