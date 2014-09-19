package jw.spacedistortion.common.network.packet;

import io.netty.buffer.ByteBuf;
import jw.spacedistortion.Triplet;
import jw.spacedistortion.common.entity.EntityTransporterRings;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.relauncher.Side;

public class PacketActivateTransporterRings implements IPacket {
	public int x1;
	public int y1;
	public int z1;
	public int x2;
	public int y2;
	public int z2;
	
	public PacketActivateTransporterRings(int x1, int y1, int z1, int x2, int y2, int z2) {
		this.x1 = x1;
		this.y1 = y1;
		this.z1 = z1;
		this.x2 = x2;
		this.y2 = y2;
		this.z2 = z2;
	}
	
	public PacketActivateTransporterRings() { }
	
	@Override
	public void readBytes(ByteBuf bytes) {
		this.x1 = bytes.readInt();
		this.y1 = bytes.readInt();
		this.z1 = bytes.readInt();
		this.x2 = bytes.readInt();
		this.y2 = bytes.readInt();
		this.z2 = bytes.readInt();
	}

	@Override
	public void writeBytes(ByteBuf bytes) {
		bytes.writeInt(this.x1);
		bytes.writeInt(this.y1);
		bytes.writeInt(this.z1);
		bytes.writeInt(this.x2);
		bytes.writeInt(this.y2);
		bytes.writeInt(this.z2);
	}

	@Override
	public void onReceive(EntityPlayer player, Side side) {
		if (!player.worldObj.isRemote) {
			player.worldObj.spawnEntityInWorld(new EntityTransporterRings(
					player.worldObj, x1 + 2, y1 + 1, z1 + 2, new Triplet<Integer, Integer, Integer>(x2 + 2, y2 + 1, z2 + 2)));
			player.worldObj.spawnEntityInWorld(new EntityTransporterRings(player.worldObj, x2 + 2, y2 + 1, z2 + 2, null));
		}
	}
}
