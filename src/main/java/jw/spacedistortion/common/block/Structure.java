package jw.spacedistortion.common.block;

import java.util.HashMap;

import jw.spacedistortion.Axis;
import jw.spacedistortion.Triplet;
import net.minecraft.block.Block;
import net.minecraftforge.common.util.ForgeDirection;

public class Structure {
	Block[][] blocks;
	Triplet<Integer, Integer, Integer> firstNeighbor;
	ForgeDirection facing;
	int xOffset;
	int yOffset;
	
	public Structure(Block[][] blocks,
			Triplet<Integer, Integer, Integer> firstNeighbor, ForgeDirection facing, int xOffset, int yOffset) {
		this.blocks = blocks;
		this.firstNeighbor = firstNeighbor;
		this.facing = facing;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
}
