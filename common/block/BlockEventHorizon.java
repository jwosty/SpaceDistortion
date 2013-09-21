package jw.spacedistortion.common.block;

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
		if (!world.isRemote) {
			TileEntityEventHorizon tileEntity = (TileEntityEventHorizon) world
					.getBlockTileEntity(x, y, z);
			if (entity instanceof EntityPlayerMP) {
				// Teleport the entity as a player
				EntityPlayer player = (EntityPlayer) entity;
				((EntityPlayerMP) player).playerNetServerHandler
						.setPlayerLocation(0, 56, 0, player.rotationYaw,
								player.rotationPitch);
			} else {
				// Teleport the entity as anything else
				entity.setPosition(0, 56, 0);
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
