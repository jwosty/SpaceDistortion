package jw.spacedistortion.client.audio;

import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class LoopingSound extends PositionedSound implements ITickableSound {
	public boolean donePlaying = false;
	
	public LoopingSound(ResourceLocation path, float volume, float pitch, int x, int y, int z) {
		super(path);
		this.repeat = true;
		this.volume = volume;
		this.field_147663_c = pitch;
		this.xPosF = x;
		this.yPosF = y;
		this.zPosF = z;
		this.field_147665_h = 0;
	}
	
	public LoopingSound(String path, float volume, float pitch, int x, int y, int z) {
		this(new ResourceLocation(path), volume, pitch, x, y, z);
	}

	@Override
	public void update() { }
	
	@Override
	public boolean isDonePlaying() {
		return this.donePlaying;
	}
}