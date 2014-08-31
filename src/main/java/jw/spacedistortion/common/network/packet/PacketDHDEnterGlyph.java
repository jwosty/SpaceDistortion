package jw.spacedistortion.common.network.packet;

import io.netty.buffer.ByteBuf;

import java.util.HashMap;

import jw.spacedistortion.Triplet;
import jw.spacedistortion.client.SDSoundHandler;
import jw.spacedistortion.common.SpaceDistortion;
import jw.spacedistortion.common.block.BlockStargateController;
import jw.spacedistortion.common.block.SDBlock;
import jw.spacedistortion.common.block.Structure;
import jw.spacedistortion.common.tileentity.StargateControllerState.StargateControllerValid.StargateControllerActive;
import jw.spacedistortion.common.tileentity.StargateControllerState.StargateControllerValid.StargateControllerReady;
import jw.spacedistortion.common.tileentity.TileEntityStargateController;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import cpw.mods.fml.relauncher.Side;

public class PacketDHDEnterGlyph implements IPacket {
	/** The x coordinate of the DHD */
	public int x;
	/** The y coordinate of the DHD */
	public int y;
	/** The z coordinate of the DHD */
	public int z;
	/** The ID of the glyph to enter into the DHD */
	public byte glyphID;
	
	public PacketDHDEnterGlyph(int x, int y, int z, byte glyphID) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.glyphID = glyphID;
	}
	
	public PacketDHDEnterGlyph() { }
	
	@Override
	public void readBytes(ByteBuf bytes) {
		this.x = bytes.readInt();
		this.y = bytes.readInt();
		this.z = bytes.readInt();
		this.glyphID = bytes.readByte();
	}

	@Override
	public void writeBytes(ByteBuf bytes) {
		bytes.writeInt(this.x);
		bytes.writeInt(this.y);
		bytes.writeInt(this.z);
		bytes.writeByte(this.glyphID);
	}
	
	@Override
	public void onReceive(EntityPlayer player, Side side) {
		if (!player.worldObj.isRemote) {
			TileEntityStargateController tileEntity = (TileEntityStargateController) player.worldObj.getTileEntity(this.x, this.y, this.z);
			if (this.glyphID == 39) {
				this.toggleStargatePair(player, tileEntity);
			} else if (tileEntity.state instanceof StargateControllerReady) {
				StargateControllerReady state = (StargateControllerReady) tileEntity.state;
				// Add another glyph into the dialing address
				if (state.currentGlyphIndex < 7) {
					state.addressBuffer[state.currentGlyphIndex] = this.glyphID;
					state.currentGlyphIndex++;
				}
				
				// Play a glyph press sound on all clients at the stargate controller block
				SDSoundHandler.serverPlaySoundToPlayers(
						player.worldObj.playerEntities, "stargate.glyph", 1F, 1F,
						(double) tileEntity.xCoord, (double) tileEntity.yCoord, (double) tileEntity.zCoord);	
			}
		}
	}
	
	/** Attempts to activate or deactivate the given stargate and the stargate it is in the process of dialing, returning whether or not it succeeded */
	public boolean toggleStargatePair(EntityPlayer player,
			TileEntityStargateController srcController) {
		// Activate or deactivate the stargates if possible
		if (srcController.state instanceof StargateControllerReady) {
			StargateControllerReady state = (StargateControllerReady)srcController.state;
			Triplet<Integer, Integer, Integer> decodedAddress = SDBlock.stargateController.decodeAddress(state.addressBuffer);
			int dstDimension = decodedAddress.X;
			// Get chunk coordinates
			int dstChunkX = decodedAddress.Y;
			int dstChunkZ = decodedAddress.Z;
			if (dstChunkX == srcController.xCoord >> 4 && dstChunkZ == srcController.zCoord >> 4) {
				// A stargate can't dial to its own chunk
				return false;
			}
			int[] dstCoords = BlockStargateController.getDominantController(player.worldObj, dstChunkX, dstChunkZ);
			if (dstCoords != null) {
				TileEntityStargateController dstController =
						(TileEntityStargateController) player.worldObj.getTileEntity(dstCoords[0], dstCoords[1], dstCoords[2]);
				SDBlock.stargateController.activateStargatePair(
						player.worldObj, srcController, dstController);
			}
		} else if (srcController.state instanceof StargateControllerActive) {
			StargateControllerActive state = (StargateControllerActive) srcController.state;
			TileEntityStargateController dstController =
					(TileEntityStargateController) player.worldObj.getTileEntity(state.connectedXCoord, state.connectedYCoord, state.connectedZCoord);
			if (dstController == null) {
				return false;
			}
			SDBlock.stargateController.deactivateStargatePair(player.worldObj, srcController, dstController);
		} else {
			return false;
		}
		return true;
	}
}
