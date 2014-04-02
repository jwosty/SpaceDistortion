package jw.spacedistortion.common.block;

import jw.spacedistortion.Axis;
import jw.spacedistortion.Triplet;
import net.minecraft.block.Block;

public class DetectStructureResults {
	Block[][] blocks;
	Triplet<Integer, Integer, Integer> firstNeighbor;
	Axis axis;
	int xOffset;
	int yOffset;
	
	public DetectStructureResults(Block[][] blocks, Triplet<Integer, Integer, Integer> firstNeighbor, Axis axis, int xOffset, int yOffset) {
		this.blocks = blocks;
		this.firstNeighbor = firstNeighbor;
		this.axis = axis;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
}
