package jw.spacedistortion.common.network.packet;

import io.netty.buffer.ByteBuf;
import jw.spacedistortion.Triplet;
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
				Triplet<Integer, Integer, Integer> decodedAddress = SDBlock.stargateController.decodeAddress(tileEntity.dialingAddress);
				int dstDimension = decodedAddress.X;
				// Get chunk coordinates
				int dstChunkX = decodedAddress.Y;
				int dstChunkZ = decodedAddress.Z;
				int[] dstCoords = BlockStargateController.getDominantController(player.worldObj, dstChunkX, dstChunkZ);
				if (dstCoords == null) {
					return;
				}
				TileEntityStargateController dstTileEntity = (TileEntityStargateController) player.worldObj.getTileEntity(dstCoords[0], dstCoords[1], dstCoords[2]);
				
				// Activate or deactivate the stargates if possible
				if (dstCoords != null) {
					switch (tileEntity.state) {
					case READY:
						SDBlock.stargateController.serverActivateStargatePair(player.worldObj, this.x, this.y, this.z, dstCoords[0], dstCoords[1], dstCoords[2]);
						break;
					case ACTIVE_OUTGOING:
						SDBlock.stargateController.serverDeactivateStargatePair(player.worldObj, this.x, this.y, this.z, dstCoords[0], dstCoords[1], dstCoords[2]);
						break;
					default:
					}
				}
				
				
			} else {
				// Add another glyph into the dialing address
				if (tileEntity.currentGlyphIndex < 7) {
					tileEntity.dialingAddress[tileEntity.currentGlyphIndex] = this.glyphID;
					tileEntity.currentGlyphIndex++;
				}
				
				// Play sounds to all players
				for (int i = 0; i < player.worldObj.playerEntities.size(); i++) {
					EntityPlayerMP p = (EntityPlayerMP) player.worldObj.playerEntities.get(i);
					p.playerNetServerHandler.sendPacket(new S29PacketSoundEffect(
							CommonProxy.MOD_ID + ":stargate.dhd.glyph",
							(double) tileEntity.xCoord, (double) tileEntity.yCoord, (double) tileEntity.zCoord,
							1.0F, 1.0F));
				}				
			}
			// Send changes on the stargate controller block and tile entity to all clients
			player.worldObj.markBlockForUpdate(this.x, this.y, this.z);
		}
	}
}
