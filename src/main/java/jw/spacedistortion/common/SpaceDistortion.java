package jw.spacedistortion.common;

import java.util.HashMap;

import jw.spacedistortion.Pair;
import jw.spacedistortion.StringGrid;
import jw.spacedistortion.client.gui.SDGuiHandler;
import jw.spacedistortion.common.block.SDBlock;
import jw.spacedistortion.common.entity.EntityTransporterRings;
import jw.spacedistortion.common.generator.GeneratorOre;
import jw.spacedistortion.common.generator.goauldbase.GeneratorGoauldBase;
import jw.spacedistortion.common.item.SDItem;
import jw.spacedistortion.common.network.ChannelHandler;
import jw.spacedistortion.common.tileentity.TileEntityEventHorizon;
import jw.spacedistortion.common.tileentity.TileEntityStargateController;
import net.minecraft.block.Block;
import net.minecraftforge.common.config.Configuration;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = CommonProxy.MOD_ID, name = CommonProxy.MOD_NAME, version = CommonProxy.MOD_VERSION)
public class SpaceDistortion {
	@Instance(CommonProxy.MOD_ID)
	public static SpaceDistortion instance;
	
	@SidedProxy(clientSide="jw.spacedistortion.client.ClientProxy", serverSide="jw.spacedistortion.common.CommonProxy")
	public static CommonProxy proxy;
	
	public static GeneratorOre oreGen = new GeneratorOre();
	public static GeneratorGoauldBase goauldBaseGen = new GeneratorGoauldBase();
	
	public static StringGrid stargateRingShape = new StringGrid(
			"  RCR  ",
			" C   C ",
			"R     R",
			"C     C",
			"R     R",
			" C   C ",
			"  RCR  ");
	public static StringGrid stargateEventHorizonShape = new StringGrid(
			"       ",
			"  EEE  ",
			" EEEEE ",
			" EEEEE ",
			" EEEEE ",
			"  EEE  ",
			"       ");
	public static StringGrid transporterRingsShape = new StringGrid(
			"TTTT",
			"T  T",
			"T  T",
			"TTTT");
	/** The mapping from template chars to the blocks they represent, and whether or not
	 * it has a direction (e.g. pistons, furnaces, etc) */
	public static HashMap<Character, Pair<Block, Boolean>> templateBlockInfo = null;
	
	@EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		//ChannelHandler.initChannels();
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		SDBlock.configureBlocks(config);
		SDItem.registerItems();
		SDBlock.registerBlocks();
		config.save();
		
		templateBlockInfo = new HashMap();
		templateBlockInfo.put('R', new Pair<Block, Boolean>(SDBlock.stargateRing, false));
		templateBlockInfo.put('C', new Pair<Block, Boolean>(SDBlock.stargateRingChevron, true));
		templateBlockInfo.put('T', new Pair<Block, Boolean>(SDBlock.ringPlatform, false));
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		SDItem.registerRecipes();
		// World gen
		GameRegistry.registerWorldGenerator(this.oreGen, 5);
		GameRegistry.registerWorldGenerator(this.goauldBaseGen, 8);
		// Tile entities
		GameRegistry.registerTileEntity(TileEntityEventHorizon.class, "tileEntityEventHorizon");
		GameRegistry.registerTileEntity(TileEntityStargateController.class, "tileEntityStargateController");
		// Entities
		EntityRegistry.registerModEntity(EntityTransporterRings.class, "transporterRings", 0, instance, 80, 1, false);
		//EntityRegistry.registerModEntity(EntityTransporterRingsPart.class, "transporterRingsPart", 0, instance, 0, 1, true);
		// GUIs
		NetworkRegistry.INSTANCE.registerGuiHandler(SpaceDistortion.instance, new SDGuiHandler());
		// Entity renderers
		proxy.registerRenderers();
		// Packet handler
		ChannelHandler.initChannels();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) { }
}