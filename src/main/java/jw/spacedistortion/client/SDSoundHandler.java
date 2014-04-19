package jw.spacedistortion.client;

import java.util.List;

import jw.spacedistortion.common.CommonProxy;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.world.World;

public class SDSoundHandler {
	public static String ensureSoundNamePrefix(String sound) {
		String[] parts = sound.split(":");
		if (parts.length == 1) {
			sound = CommonProxy.MOD_ID;
			// Add the rest of the string back
			for (String part : parts) {
				sound += ":" + part;
			}
			
		}
		return sound;
	}
	
	public static void playSoundAtPosition(World world, int x, int y, int z, String sound) {
		world.playSoundEffect(
				(double)x + 0.5D, (double)y + 0.5D, (double)z + 0.5D,
				SDSoundHandler.ensureSoundNamePrefix(sound),
				1.0F, 1.25F);
	}
	
	public static void serverPlaySoundToPlayers(List players, String sound, float volume, float pitch, double x, double y, double z) {
		for (int i = 0; i < players.size(); i++) {
			((EntityPlayerMP) players.get(i)).playerNetServerHandler.sendPacket(new S29PacketSoundEffect(
					SDSoundHandler.ensureSoundNamePrefix(sound),
					x, y, z, volume, pitch));
		}
	}
}
