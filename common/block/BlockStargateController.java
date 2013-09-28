package jw.spacedistortion.common.block;

import java.util.ArrayList;
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
		// Get the target stargate center coordinates for use in both stargates'
		// filling code (coords of target, tile entity of src)
		ArrayList<Triplet<Integer, Integer, Integer>> dstBlocks = this
				.getStargateCenterBlocks(world, targetX, targetY, targetZ);

		// Fill the dialing stargate
		ArrayList<Triplet<Integer, Integer, Integer>> srcBlocks = this
				.getStargateCenterBlocks(world, srcX, srcY, srcZ);
		for (int i = 0; i < srcBlocks.size(); i++) {
			Triplet<Integer, Integer, Integer> srcBlockCoords = srcBlocks.get(i);
			Triplet<Integer, Integer, Integer> dstBlockCoords = dstBlocks.get(i);
			System.out.println("srcBlockCoords -> " + srcBlockCoords);
			// Set the destination blocks
			world.setBlock(srcBlockCoords.X, srcBlockCoords.Y, srcBlockCoords.Z,
					SDBlock.eventHorizon.blockID);
			// Set the tile entity that stores the specific destination coordinates
			//TileEntityEventHorizon tileEntity = new TileEntityEventHorizon();
			TileEntityEventHorizon tileEntity = (TileEntityEventHorizon) world
					.getBlockTileEntity(srcBlockCoords.X, srcBlockCoords.Y, srcBlockCoords.Z);
			if (tileEntity != null) {
				tileEntity.isOutgoing = true;
				tileEntity.destX = dstBlockCoords.X;
				tileEntity.destY = dstBlockCoords.Y;
				tileEntity.destZ = dstBlockCoords.Z;
				System.out.println("dstBlockCoords -> " + dstBlockCoords);
			}
			
			// Fill the target stargate with "dummy" event horizon blocks
			world.setBlock(dstBlockCoords.X, dstBlockCoords.Y, dstBlockCoords.Z, SDBlock.eventHorizon.blockID);
		}
	}
	
	/**
	 * Returns the coordinates of all the center blocks at the given stargate, or an empty ArrayList if there's no stargate there.
	 * @param world The world that the stargate is located in
	 * @param x The x coordinate of the stargate controller
	 * @param y The y coordinate of the stargate controller
	 * @param z The z coordinate of the stargate controller
	 * @return
	 */
	public ArrayList<Triplet<Integer, Integer, Integer>> getStargateCenterBlocks(World world, int x, int y, int z) {
		ArrayList<Triplet<Integer, Integer, Integer>> results = new ArrayList();
		// See if there's really a stargate here
		DetectStructureResults stargate = this.getStargateBlocks(
				world, x, y, z);
		Integer[] firstNeighbor = this.getNeighboringBlocks(world, x, y, z)
				.get(0);
		Triplet<Integer, Integer, Integer> relativeOrigin = this
				.templateToWorldCoordinates(-stargate.xOffset,
						stargate.yOffset, stargate.plane);
		Triplet<Integer, Integer, Integer> origin = new Triplet<Integer, Integer, Integer>(
				firstNeighbor[0] + relativeOrigin.X, firstNeighbor[1]
						+ relativeOrigin.Y, firstNeighbor[2] + relativeOrigin.Z);
		for (int templateX = 0; templateX <= stargateEventHorizonShape.width; templateX++) {
			for (int templateY = 0; templateY <= stargateEventHorizonShape.height; templateY++) {
				if (stargateEventHorizonShape.get(templateX, templateY) == 'X') {
					int[] coords = this.getBlockInStructure(world, origin.X,
							origin.Y, origin.Z, templateX,
							-templateY, stargate.plane);
					results.add(new Triplet(coords[0], coords[1], coords[2]));
				}
			}
		}
		return results;
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
