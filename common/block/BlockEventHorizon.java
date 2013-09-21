package jw.spacedistortion.common.block;

import jw.spacedistortion.common.tileentity.TileEntityEventHorizon;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
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
		// this is where the code to teleport the player to the other stargate will come in, but for now there's nothing here
		entity.setInWeb();
	}
	
	// Returns true if the given side of this block type should be rendered, if the adjacent block is at the given coordinates
	@Override
	public boolean shouldSideBeRendered(IBlockAccess blockAccess, int x, int y, int z, int side) {
		int bid = blockAccess.getBlockId(x, y, z);
		return !(bid == this.blockID || bid == SDBlock.stargateRing.blockID);
	}
}
