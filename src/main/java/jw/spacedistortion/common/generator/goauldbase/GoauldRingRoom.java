package jw.spacedistortion.common.generator.goauldbase;

import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class GoauldRingRoom extends GoauldRoom {
	public GoauldRingRoom() { super(); }
	public GoauldRingRoom(boolean[] connections) { super(connections); }
	
	@Override
	public void buildInWorld(World world, int x, int y, int z) {
		this.buildCorridor(world, x, y, z);
		this.buildRingArea(world, x, y, z);
		for (int i = 0; i < connections.length; i++) {
			if (this.connections[i]) {
				this.buildConnection(world, x, y, z, ForgeDirection.getOrientation(i + 2));
			} else {
				this.buildEnd(world, x, y, z, ForgeDirection.getOrientation(i + 2));
			}
		}
	}
	
	public void buildCorridor(World world, int x, int y, int z) {
		// Floor and ceiling
		for (int xo = -3; xo < 4; xo++) {
			for (int yo = -3; yo < 2; yo++) {
				for (int zo = -3; zo < 4; zo++) {
					if (Math.abs(xo) + Math.abs(zo) != 6) {
						if (yo == -3) world.setBlock(x + xo, y + yo, z + zo, Blocks.sandstone);
						else if (yo == 1) world.setBlock(x + xo, y + yo, z + zo, Blocks.stained_hardened_clay, 1, 2);
						else world.setBlockToAir(x + xo, y + yo, z + zo);
					}
				}
			}
		}
		// Corners
		for (int xo : new int[] { -3, -2, 2, 3 }) {
			for (int yo = -2; yo < 1; yo++) {
				for (int zo : new int[] { -3, -2, 2, 3 }) {
					if (Math.abs(xo) + Math.abs(zo) == 5) {
						world.setBlock(x + xo, y + yo, zo + z, Blocks.stained_hardened_clay, 1, 2);
					}
				}
			}
		}
		// Clear out some more blocks
		for (ForgeDirection d : GeneratorGoauldBase.possibleConnections) {
			for (int s = -1; s < 2; s++) {
				for (int yo = -2; yo < 1; yo++) {
					world.setBlockToAir(x + this.getbx(d, s, 4), y + yo, z + this.getbz(d, s, 4));
				}
			}
		}
		// Torches
		for (int xo : new int[] { -3, -1, 1, 3 }) {
			for (int zo : new int[] { -3, -1, 1, 3 }) {
				if (Math.abs(xo) + Math.abs(zo) == 4) {
					world.setBlock(x + xo, y - 1, z + zo, Blocks.torch);
				}
			}
		}
	}
	
	public void buildEnd(World world, int x, int y, int z, ForgeDirection d) {
		for (int s = -1; s < 2; s++) {
			for (int yo = -2; yo < 1; yo++) {
				world.setBlock(x + this.getbx(d, s, 4), y + yo, z + this.getbz(d, s, 4), Blocks.stained_hardened_clay, 1, 2);
			}
		}
	}
	
	public void buildConnection(World world, int x, int y, int z, ForgeDirection d) {
		// Floor and ceiling
		for (int s = -1; s < 2; s++) {
			int bx = x + this.getbx(d, s, 4);
			int bz = z + this.getbz(d, s, 4);
			world.setBlock(bx, y - 3, bz, Blocks.sandstone);
			world.setBlock(bx, y + 1, bz, Blocks.stained_hardened_clay, 1, 2);
		}
		// Walls
		for (int yo = -2; yo < 1; yo++) {
			world.setBlock(x + this.getbx(d, -2, 4), y + yo, z + this.getbz(d, -2, 4), Blocks.stained_hardened_clay, 2, 2);
			world.setBlock(x + this.getbx(d, 2, 4), y + yo, z + this.getbz(d, 2, 4), Blocks.stained_hardened_clay, 1, 2);
		}
	}
	
	public void buildRingArea(World world, int x, int y, int z) {
		
	}
}