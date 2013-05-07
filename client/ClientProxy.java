package jw.spacedistortion.client;

import jw.spacedistortion.client.texturefx.TextureEventHorizonFX;
import jw.spacedistortion.common.CommonProxy;
import net.minecraft.src.ModLoader;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerRenderers() {
		MinecraftForgeClient.preloadTexture(TEXTURES_PNG);
		//Minecraft.getMinecraft().renderEngine.registerTextureFX(new TextureEventHorizonFX());
		ModLoader.addAnimation(new TextureEventHorizonFX());
	}
}
