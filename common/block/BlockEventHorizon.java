package jw.spacedistortion.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

public class BlockEventHorizon extends SDBlock {
	// The coordinate at which the textures for this block starts
	private int blockIndexInTexture;
	
	public BlockEventHorizon(int id, int _blockIndexInTexture) {
		super(id, _blockIndexInTexture, Material.portal);
		blockIndexInTexture = _blockIndexInTexture;
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
}
