package jw.spacedistortion.common.block;

import jw.spacedistortion.Triplet;

public class DetectStructureResults {
	boolean[][] blocks;
	int plane;
	int xOffset;
	int yOffset;
	
	public DetectStructureResults(boolean[][] blocks, int plane, int xOffset, int yOffset) {
		this.blocks = blocks;
		this.plane = plane;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
}
