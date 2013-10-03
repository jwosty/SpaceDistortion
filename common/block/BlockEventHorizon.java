package jw.spacedistortion.common.block;

import jw.spacedistortion.Axis;
import jw.spacedistortion.common.tileentity.TileEntityEventHorizon;
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
	
	public BlockEventHorizon(int id) {
		super(id,  Material.portal);
	}
	
	@Override
	public TileEntity createNewTileEntity(World world) {
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
	public void onEntityCollidedWithBlock(World world, int x, int y, int z, Entity entity) {
		// Tile entities of source and target blocks
		TileEntityEventHorizon srcTileEntity = (TileEntityEventHorizon) world
				.getBlockTileEntity(x, y, z);
		TileEntityEventHorizon dstTileEntity = (TileEntityEventHorizon) world
				.getBlockTileEntity(srcTileEntity.destX, srcTileEntity.destY,
						srcTileEntity.destZ);
		if (!world.isRemote && srcTileEntity.isOutgoing) {
			System.out.println("Entity pitch -> " + entity.rotationPitch + ", Entity yaw -> " + entity.rotationYaw);
			
			// Planes of source and target stargates
			Axis srcPlane = srcTileEntity.axis;
			Axis dstPlane = dstTileEntity.axis;
			System.out.println("srcPlane -> " + srcPlane + ", dstPlane -> " + dstPlane);
			double entityX = srcTileEntity.destX
					+ (entity.posX - Math.floor(entity.posX));
			double entityY = srcTileEntity.destY
					- (entity.posY - Math.floor(entity.posY)) - 1;
			double entityZ = srcTileEntity.destZ
					+ (entity.posZ - Math.floor(entity.posZ));
			if (entity instanceof EntityPlayerMP) {
				// Teleport the entity as a player
				EntityPlayer player = (EntityPlayer) entity;
				((EntityPlayerMP) player).playerNetServerHandler
						.setPlayerLocation(entityX, entityY, entityZ, player.rotationYaw,
								player.rotationPitch);
			} else {
				// Teleport the entity as anything else
				entity.setPosition(entityX, entityY, entityZ);
			}
		}
	}
	
	// Returns true if the given side of this block type should be rendered, if the adjacent block is at the given coordinates
	@Override
	public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
		int bid = blockAccess.getBlockId(x, y, z);
		return !(bid == this.blockID || bid == SDBlock.stargateRing.blockID);
	}
}
