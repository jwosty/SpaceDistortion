package jw.spacedistortion.common;

import net.minecraft.client.Minecraft;
import net.minecraft.src.BaseMod;
import jw.spacedistortion.common.block.SDBlock;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid="SpaceDistortion", name="Space Distortion", version="0.0.0")
public class SpaceDistortion extends BaseMod {
	// The instance of this mod that Forge uses
	@Instance("SpaceDistortion")
	public static SpaceDistortion instance;
	
	@SidedProxy(clientSide="jw.spacedistortion.client.ClientProxy", serverSide="jw.spacedistortion.common.CommonProxy")
	public static CommonProxy proxy;
	
	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		// Stub method
	}
	
	@Init
	// Not sure whether to use this or load if you use both the FML mod api or the Minecraft ModLoader api... :/
	public void init(FMLInitializationEvent event) {
		proxy.registerRenderers();
		SDBlock.addBlocks();
	}
	
	@Override
	// See SpaceDistortion#init
	public void load() {
	}
	
	@PostInit
	public void postInit(FMLPostInitializationEvent event) {
		// Stub method
	}

	@Override
	public String getVersion() {
		return "Space Distortion mod v0.0.0 for Minecraft 1.4.7";
	}
}