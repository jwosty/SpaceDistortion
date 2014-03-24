package jw.spacedistortion.common.block;

import jw.spacedistortion.Axis;
import jw.spacedistortion.Pair;
import jw.spacedistortion.client.SDSoundHandler;
import jw.spacedistortion.common.network.ChannelHandler;
import jw.spacedistortion.common.network.packet.PacketPlayLoopableTileEntitySound;
import jw.spacedistortion.common.tileentity.TileEntityEventHorizon;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockEventHorizon extends SDBlock implements ITileEntityProvider {
	
	public BlockEventHorizon() {
		super(Material.portal);
	}
	
	@Override
	public boolean hasTileEntity() {
		return true;
	}
	
	@Override
	public TileEntity createNewTileEntity(World world, int metadata) {
		return new TileEntityEventHorizon();
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
	@Override
	public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
		return null;
	}
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		if (!world.isRemote) {
			//ChannelHandler.serverSendPacketAllClients(new PacketPlayLoopableTileEntitySound(x, y, z));
		}
	}
	
	@Override
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		// Tile entities of source and target blocks
		TileEntityEventHorizon srcTileEntity = (TileEntityEventHorizon) world
				.getTileEntity(x, y, z);
		TileEntityEventHorizon dstTileEntity = (TileEntityEventHorizon) world
				.getTileEntity(srcTileEntity.destX, srcTileEntity.destY,
						srcTileEntity.destZ);
		if (!world.isRemote && srcTileEntity.isOutgoing && dstTileEntity != null
				&& Math.floor(entity.posX) == x && Math.floor(entity.posY) == y
				&& Math.floor(entity.posZ) == z) {
			// Planes of source and target stargates
			Axis srcPlane = srcTileEntity.axis;
			Axis dstPlane = dstTileEntity.axis;
			System.out.println("srcPlane -> " + srcPlane + ", dstPlane -> " + dstPlane);
			// Calculate position
			double entityX = srcTileEntity.destX
					+ (entity.posX - Math.floor(entity.posX));
			double entityY = srcTileEntity.destY
					- (entity.posY - Math.floor(entity.posY));
			double entityZ = srcTileEntity.destZ
					+ (entity.posZ - Math.floor(entity.posZ));
			
			// Calculate rotation
			Pair<Integer, Integer> srcRotation = srcPlane.getPitchAndYaw();
			Pair<Integer, Integer> dstRotation = dstPlane.getPitchAndYaw();
			float entityPitch = entity.rotationPitch;
			float entityYaw = (srcRotation.Y - (entity.rotationYaw + 180)) + dstRotation.Y;
			
			
			if (entity instanceof EntityPlayerMP) {
				// Teleport the entity as a player
				EntityPlayer player = (EntityPlayer) entity;
				((EntityPlayerMP) player).playerNetServerHandler
						.setPlayerLocation(entityX, entityY, entityZ, entityYaw,
								entityPitch);
			} else {
				// Teleport the entity as anything else
				entity.setPositionAndRotation(entityX, entityY, entityZ, entityYaw, entityPitch);
			}
			// Play the stargate enter/exit sound at both stargates
			SDSoundHandler.serverPlaySoundToPlayers(world.playerEntities, "stargate.enter", 1F, 1F, x, y, z);
			SDSoundHandler.serverPlaySoundToPlayers(world.playerEntities, "stargate.enter", 1F, 1F, entityX, entityY, entityZ);
		}
	}
	
	// Returns true if the given side of this block type should be rendered, if the adjacent block is at the given coordinates
	@Override
	public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
		Block block = blockAccess.getBlock(x, y, z);
		return block != this;
	}
}
