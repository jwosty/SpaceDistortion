package jw.spacedistortion.common.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import jw.spacedistortion.Pair;
import jw.spacedistortion.StringGrid;
import jw.spacedistortion.Triplet;
import jw.spacedistortion.common.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class SDBlock extends Block {
	public static BlockStargateController stargateController;
	public static BlockStargateRing stargateRing;
	public static BlockStargateRingChevron stargateRingChevron;
	public static BlockEventHorizon eventHorizon;

	/**
	 * Create the block objects based on configuration information
	 * @param config The configuration file to use
	 */
	public static void configureBlocks(Configuration config) {
		stargateController = (BlockStargateController) new BlockStargateController()
				.setBlockName("stargateController")
				.setCreativeTab(CreativeTabs.tabBlock)
				.setStepSound(Block.soundTypeStone);
		stargateRing = (BlockStargateRing) new BlockStargateRing()
				.setBlockName("stargateRing")
				.setCreativeTab(CreativeTabs.tabBlock)
				.setStepSound(Block.soundTypeStone);
		stargateRingChevron = (BlockStargateRingChevron) new BlockStargateRingChevron()
				.setBlockName("stargateRingChevron")
				.setCreativeTab(CreativeTabs.tabBlock)
				.setStepSound(Block.soundTypeStone);
		eventHorizon = (BlockEventHorizon) new BlockEventHorizon()
				.setBlockName("eventHorizon")
				.setStepSound(Block.soundTypeGlass)
				.setLightLevel(0.875f);
	}
	
	public SDBlock(Material material) {
		super(material);
	}

	/**
	 * For use in registerIcons, but doesn't actually register the icon and just
	 * returns the icon name
	 * 
	 * @param side The side of the block
	 * @param metadata Block metadata
	 * @return The icon name (for use in registerIcon)
	 */
	public String getIconName() {
		return CommonProxy.MOD_ID + ":" + (this.getUnlocalizedName().substring(5));
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister register) {
		this.blockIcon = register.registerIcon(this.getIconName());
	}
	
	/**
	 * Registers all blocks in the mod and the names for the blocks
	 */
	public static void registerBlocks() {
		GameRegistry.registerBlock(stargateController, stargateController.getUnlocalizedName());
		GameRegistry.registerBlock(stargateRing, stargateRing.getUnlocalizedName());
		GameRegistry.registerBlock(stargateRingChevron, stargateRingChevron.getUnlocalizedName());
		GameRegistry.registerBlock(eventHorizon, eventHorizon.getUnlocalizedName());
	}

	/**
	 * Synchronizes a TileEntity with all clients. Only works server-side; will throw an error for client-side!
	 * @param tileEntity
	 */
	public static void syncTileEntity(TileEntity tileEntity) {
		if (tileEntity != null) {
			Packet packet = tileEntity.getDescriptionPacket();
			
			//PacketDispatcher.sendPacketToAllPlayers(packet);
		}
	}
	
	public void updateNearbyStargateControllers(World world, int x, int y, int z) {
		Block thisBlock = world.getBlock(x, y, z);
		for (int xx = (x-4); xx < (x+5); xx++) {
			for (int yy = (y-4); yy < (y+5); yy++) {
				for (int zz = (z-4); zz < (z+5); zz++) {
					Block block = world.getBlock(xx, yy, zz);
					if (block == SDBlock.stargateController && !(world.isRemote)) {
						world.notifyBlockOfNeighborChange(xx, yy, zz, SDBlock.stargateController);
						world.markBlockForUpdate(xx, yy, zz);
						SDBlock.syncTileEntity(world.getTileEntity(xx, yy, zz));
					}
				}
			}
		}
	}
}