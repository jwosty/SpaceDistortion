package jw.spacedistortion.common.tileentity;

import jw.spacedistortion.Axis;
import jw.spacedistortion.client.audio.LoopingSound;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntityEventHorizon extends TileEntity {
	public boolean isOutgoing = false;
	public Axis axis;
	// Coordinates of the destination stargate controller
	public int destX;
	public int destY;
	public int destZ;
	
	public boolean shouldPlaySound = false;
	@SideOnly(Side.CLIENT)
	public LoopingSound soundLoop;

	public TileEntityEventHorizon() { };
	
	public TileEntityEventHorizon(boolean shouldPlaySound) {
		this.shouldPlaySound = shouldPlaySound;
	}
	
	@Override
	public void writeToNBT(NBTTagCompound data) {
		super.writeToNBT(data);
		data.setBoolean("outgoing", isOutgoing);
		if (axis != null) {
			data.setInteger("axis", this.axis.toInt());
		}
		data.setIntArray("destCoords", new int[]{this.destX, this.destY, this.destZ});
		data.setBoolean("shouldPlaySound", this.shouldPlaySound);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound data) {
		super.readFromNBT(data);
		this.isOutgoing = data.getBoolean("outgoing");
		Integer a = data.getInteger("axis");
		if (a != null) {
			this.axis = Axis.ofInt(a);
		}
		int[] destCoords = data.getIntArray("destCoords");
		this.destX = destCoords[0];
		this.destY = destCoords[1];
		this.destZ = destCoords[2];
		
		this.shouldPlaySound = data.getBoolean("shouldPlaySound");
	}
	
	@Override
	public void validate() {
		super.validate();
		if (this.worldObj.isRemote && this.shouldPlaySound) {
			if (this.soundLoop == null) {
				this.soundLoop = new LoopingSound("spacedistortion:stargate.eventhorizon", 1F, 1, this.xCoord, this.yCoord, this.zCoord);
			}
			SoundHandler soundHandler = Minecraft.getMinecraft().getSoundHandler();
			if (!soundHandler.isSoundPlaying(this.soundLoop)) {
				soundHandler.playSound(this.soundLoop);
			}
		}
	}
	
	@Override
	public void invalidate() {
		super.invalidate();
		if (this.worldObj.isRemote && this.soundLoop != null) {
			Minecraft.getMinecraft().getSoundHandler().stopSound(this.soundLoop);
		}
	}
}