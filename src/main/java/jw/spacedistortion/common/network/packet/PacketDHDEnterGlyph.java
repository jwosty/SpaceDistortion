package jw.spacedistortion.common.network.packet;

import io.netty.buffer.ByteBuf;
import jw.spacedistortion.Triplet;
import jw.spacedistortion.client.SDSoundHandler;
import jw.spacedistortion.common.CommonProxy;
import jw.spacedistortion.common.block.BlockStargateController;
import jw.spacedistortion.common.block.SDBlock;
import jw.spacedistortion.common.tileentity.TileEntityStargateController;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S29PacketSoundEffect;
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
			} else {
				// Add another glyph into the dialing address
				if (tileEntity.currentGlyphIndex < 7) {
					tileEntity.dialingAddress[tileEntity.currentGlyphIndex] = this.glyphID;
					tileEntity.currentGlyphIndex++;
				}
				
				// Play a glyph press sound on all clients at the stargate controller block
				SDSoundHandler.serverPlaySoundToPlayers(
						player.worldObj.playerEntities, "stargate.glyph", 1F, 1F,
						(double) tileEntity.xCoord, (double) tileEntity.yCoord, (double) tileEntity.zCoord);	
			}
			// Send changes on the stargate controller block and tile entity to all clients
			player.worldObj.markBlockForUpdate(this.x, this.y, this.z);
		}
	}
	
	/** Attempts to activate or deactivate the given stargate and the stargate it is in the process of dialing, returning whether or not it succeeded */
	public boolean toggleStargatePair(EntityPlayer player,
			TileEntityStargateController tileEntity) {
		Triplet<Integer, Integer, Integer> decodedAddress = SDBlock.stargateController.decodeAddress(tileEntity.dialingAddress);
		int dstDimension = decodedAddress.X;
		// Get chunk coordinates
		int dstChunkX = decodedAddress.Y;
		int dstChunkZ = decodedAddress.Z;
		if (dstChunkX == tileEntity.xCoord >> 4 && dstChunkZ == tileEntity.zCoord >> 4) {
			// A stargate can't dial to its own chunk
			return false;
		}
		int[] dstCoords = BlockStargateController.getDominantController(player.worldObj, dstChunkX, dstChunkZ);
		if (dstCoords == null) {
			return false;
		}
		TileEntityStargateController dstTileEntity = (TileEntityStargateController) player.worldObj.getTileEntity(dstCoords[0], dstCoords[1], dstCoords[2]);
		
		// Activate or deactivate the stargates if possible
		if (dstCoords != null) {
			switch (tileEntity.state) {
			case READY:
				SDBlock.stargateController.serverActivateStargatePair(
						player.worldObj, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord,
						dstCoords[0], dstCoords[1], dstCoords[2]);
				break;
			case ACTIVE_OUTGOING:
				SDBlock.stargateController.serverDeactivateStargatePair(
						player.worldObj, tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord,
						dstCoords[0], dstCoords[1], dstCoords[2]);
				break;
			default:
			}
		}
		return true;
	}
}
