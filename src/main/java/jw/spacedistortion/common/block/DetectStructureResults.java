package jw.spacedistortion.common.block;

import java.util.HashMap;

import jw.spacedistortion.Axis;
import jw.spacedistortion.Triplet;
import net.minecraft.block.Block;
import net.minecraftforge.common.util.ForgeDirection;

public class DetectStructureResults {
	Block[][] blocks;
	HashMap<Block, ForgeDirection> blockDirections;
	Triplet<Integer, Integer, Integer> firstNeighbor;
	Axis axis;
	int xOffset;
	int yOffset;
	
	public DetectStructureResults(Block[][] blocks, HashMap<Block, ForgeDirection> blockDirections,
			Triplet<Integer, Integer, Integer> firstNeighbor, Axis axis, int xOffset, int yOffset) {
		this.blocks = blocks;
		this.blockDirections = blockDirections;
		this.firstNeighbor = firstNeighbor;
		this.axis = axis;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
}
