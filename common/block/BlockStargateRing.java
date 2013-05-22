package jw.spacedistortion.common.block;

import jw.spacedistortion.StringGrid;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class BlockStargateRing extends SDBlock {
	public BlockStargateRing(int id, int texture) {
		super(id, texture, Material.rock);
	}

	public void onBlockAdded(World world, int x, int y, int z) {
		if (this.detectStructureAtLocation(world, new StringGrid("XXX"), x, y,
				z, -1) != null) {
			world.setBlock(x, y, z, Block.blockEmerald.blockID);
		}
	}
}
