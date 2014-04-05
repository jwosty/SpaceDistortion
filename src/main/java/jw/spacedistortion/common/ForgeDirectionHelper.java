package jw.spacedistortion.common;

import net.minecraftforge.common.util.ForgeDirection;

public class ForgeDirectionHelper {
	public static int getYaw(ForgeDirection direction) {
		switch (direction) {
		case SOUTH: return 0;
		case WEST: return 90;
		case NORTH: return 180;
		case EAST: return 270;
		default: return 0;
		}
	}
	
	public static int getPitch(ForgeDirection direction) {
		switch (direction) {
		case UP: return -90;
		case DOWN: return 90;
		default: return 0;
		}
	}
}
