package jw.spacedistortion.common;

import jw.spacedistortion.common.block.SDBlock;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;

@Mod(modid="SpaceDistortion", name="Space Distortion", version="0.0.0")
@NetworkMod(clientSideRequired=true, serverSideRequired=false, channels={"OutgoingWormhole"}, packetHandler=PacketHandler.class)
public class SpaceDistortion {
	// The instance of this mod that Forge uses
	@Instance("SpaceDistortion")
	public static SpaceDistortion instance;
	
	@SidedProxy(clientSide="jw.spacedistortion.client.ClientProxy", serverSide="jw.spacedistortion.common.CommonProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		proxy.registerRenderers();
		SDBlock.addBlocks();
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		config.save();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {}
}