package jw.spacedistortion.common.block;

import jw.spacedistortion.Axis;

public class DetectStructureResults {
	boolean[][] blocks;
	Axis axis;
	int xOffset;
	int yOffset;
	
	public DetectStructureResults(boolean[][] blocks, Axis axis, int xOffset, int yOffset) {
		this.blocks = blocks;
		this.axis = axis;
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
}
