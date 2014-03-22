package jw.spacedistortion.client;

import jw.spacedistortion.common.CommonProxy;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class SDSoundHandler {
	public static void playSoundAtPosition(World world, int x, int y, int z, String sound) {
		world.playSoundEffect(
				(double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D,
				CommonProxy.MOD_ID + ":" + sound,
				1.0F, 1.25F);//world.rand.nextFloat() * 0.25F + 0.75F);
	}
}
