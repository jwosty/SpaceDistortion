package jw.spacedistortion.common;

import jw.spacedistortion.common.block.SDBlock;
import jw.spacedistortion.common.network.PacketHandler;
import jw.spacedistortion.common.tileentity.TileEntityEventHorizon;
import jw.spacedistortion.common.tileentity.TileEntityStargateController;
import net.minecraftforge.common.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = CommonProxy.MOD_ID, name = CommonProxy.MOD_NAME, version = CommonProxy.MOD_VERSION)
@NetworkMod(clientSideRequired=true, serverSideRequired=true, channels={"OutgoingWormhole"}, packetHandler=PacketHandler.class)
public class SpaceDistortion {
	// The instance of this mod that Forge uses
	@Instance("SpaceDistortion")
	public static SpaceDistortion instance;
	
	@SidedProxy(clientSide="jw.spacedistortion.client.ClientProxy", serverSide="jw.spacedistortion.common.CommonProxy")
	public static CommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		SDBlock.configureBlocks(config);
		SDBlock.registerBlocks();
		config.save();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		GameRegistry.registerTileEntity(TileEntityEventHorizon.class, "tileEntityEventHorizon");
		GameRegistry.registerTileEntity(TileEntityStargateController.class, "tileEntityStargateController");
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {}
}