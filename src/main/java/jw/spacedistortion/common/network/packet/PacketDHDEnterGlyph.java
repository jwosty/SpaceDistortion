package jw.spacedistortion.common.network.packet;

import io.netty.buffer.ByteBuf;
import jw.spacedistortion.common.block.SDBlock;
import jw.spacedistortion.common.tileentity.TileEntityStargateController;
import net.minecraft.entity.player.EntityPlayer;
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
		// Add another glyph into the dialing address
		TileEntityStargateController tileEntity = (TileEntityStargateController) player.worldObj.getTileEntity(this.x, this.y, this.z);
		if (tileEntity.currentGlyphIndex < 7) {
			tileEntity.dialingAddress[tileEntity.currentGlyphIndex] = this.glyphID;
			tileEntity.currentGlyphIndex++;
		}
		
		// Send an update for the block and tile entity to all clients
		player.worldObj.markBlockForUpdate(this.x, this.y, this.z);
	}
}
