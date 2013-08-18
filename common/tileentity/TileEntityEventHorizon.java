package jw.spacedistortion.common.tileentity;

public class TileEntityEventHorizon extends TileEntityStargateController {
	public int xOffset;
	public int yOffset;
	
	public TileEntityEventHorizon(int xOffset, int yOffset) {
		this.xOffset = xOffset;
		this.yOffset = yOffset;
	}
}