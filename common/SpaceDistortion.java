package jw.spacedistortion.common;

import jw.spacedistortion.common.block.Blocks;
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
public class SpaceDistortion {
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
	public void init(FMLInitializationEvent event) {
		proxy.registerRenderers();
		Blocks.addBlocks();
	}
	
	@PostInit
	public void postInit(FMLPostInitializationEvent event) {
		// Stub method
	}
}