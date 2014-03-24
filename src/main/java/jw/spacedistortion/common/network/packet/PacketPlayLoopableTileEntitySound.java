package jw.spacedistortion.common.network.packet;

import io.netty.buffer.ByteBuf;
import jw.spacedistortion.client.audio.LoopableTileEntitySound;
import jw.spacedistortion.common.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.relauncher.Side;

public class PacketPlayLoopableTileEntitySound implements IPacket {
	private int x;
	private int y;
	private int z;
	
	public PacketPlayLoopableTileEntitySound(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public PacketPlayLoopableTileEntitySound(TileEntity tileEntity) {
		this(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
	}
	
	public PacketPlayLoopableTileEntitySound() { };
	
	@Override
	public void readBytes(ByteBuf bytes) {
		this.x = bytes.readInt();
		this.y = bytes.readInt();
		this.z = bytes.readInt();
	}

	@Override
	public void writeBytes(ByteBuf bytes) {
		bytes.writeInt(this.x);
		bytes.writeInt(this.y);
		bytes.writeInt(this.z);
	}

	@Override
	public void onReceive(EntityPlayer player, Side side) {
		if (player.worldObj.isRemote) {
			TileEntity tileEntity = player.worldObj.getTileEntity(this.x, this.y, this.z);
			if (tileEntity != null) {
				ISound eventHorizonSound = new LoopableTileEntitySound(CommonProxy.MOD_ID + ":stargate.eventhorizon", tileEntity, 1F, 1);
				Minecraft.getMinecraft().getSoundHandler().playSound(eventHorizonSound);
			}
		}
	}
}
