package jw.spacedistortion.common.generator.goauldbase;

import java.util.Random;

import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class GoauldRoom {
	public boolean[] connections;
	
	public GoauldRoom() { connections = new boolean[4]; }
	
	public GoauldRoom(boolean[] connections) {
		this.connections = connections;
	}
	
	public GoauldRoom setConnection(ForgeDirection direction, boolean isConnected) {
		this.connections[direction.ordinal() - 2] = isConnected;
		return this;
	}
	
	public abstract void buildInWorld(Random rand, World world, int blockOriginX, int blockOriginY, int blockOriginZ);
	
	protected int getbx(ForgeDirection direction, int sidewaysDistance, int forwardDistance) {
		if (direction == ForgeDirection.NORTH || direction == ForgeDirection.SOUTH) {
			return sidewaysDistance * -direction.offsetZ;
		} else {
			return forwardDistance * direction.offsetX;
		}
	}
	
	protected int getbz(ForgeDirection direction, int sidewaysDistance, int forwardDistance) {
		if (direction == ForgeDirection.WEST || direction == ForgeDirection.EAST) {
			return sidewaysDistance * direction.offsetX;
		} else {
			return forwardDistance * direction.offsetZ;
		}
	}
}