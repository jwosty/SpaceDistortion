package jw.spacedistortion.client;

import jw.spacedistortion.common.CommonProxy;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerRenderers() {
		MinecraftForgeClient.preloadTexture(TEXTURES_PNG);
		MinecraftForgeClient.preloadTexture(GLYPHS_PNG);
	}
}
