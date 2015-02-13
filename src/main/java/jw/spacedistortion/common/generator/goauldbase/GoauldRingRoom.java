package jw.spacedistortion.common.generator.goauldbase;

import java.util.Random;

import jw.spacedistortion.common.block.SDBlock;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class GoauldRingRoom extends GoauldRoom {
	public GoauldRingRoom() { super(); }
	public GoauldRingRoom(boolean[] connections) { super(connections); }
	
	@Override
	public void buildInWorld(Random rand, World world, int x, int y, int z) {
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
		// Inner carpet
		world.setBlock(x, y - 9 / 2 + 2, z, Blocks.carpet, 4, 2);
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
			world.setBlock(x + this.getbx(d, -2, 4), y + yo, z + this.getbz(d, -2, 4), Blocks.stained_hardened_clay, 1, 2);
			world.setBlock(x + this.getbx(d, 2, 4), y + yo, z + this.getbz(d, 2, 4), Blocks.stained_hardened_clay, 1, 2);
		}
		// Add carpet
		for (int f = 1; f < 5; f++) {
			world.setBlock(x + this.getbx(d, 0, f), y - 2, z + this.getbz(d, 0, f), Blocks.carpet, 4, 2);
		}
	}
	
	public void buildRingArea(World world, int x, int y, int z) {
		// Ladder-bounding blocks
		world.setBlock(x + 2, y + 2, z + 3, Blocks.stained_hardened_clay, 1, 2);
		world.setBlock(x + 3, y + 2, z + 2, Blocks.stained_hardened_clay, 1, 2);
		// Ladders
		for (int yo = -2; yo < 3; yo++) {
			world.setBlock(x + 2, y + yo, z + 2, Blocks.ladder, 4, 2);
		}
		for (int xo = -2; xo < 3; xo++) {
			for (int yo = 2; yo < 7; yo++) {
				for (int zo = -1; zo < 3; zo++) {
					if (yo == 2) {
						// Floor and ring
						if (!(xo == 2 && zo == 2)) {
							Block b;
							int meta = 0;
							if (xo == 1 || xo == -2 || (xo != 2 && (zo == -1 || zo == 2))) {
								b = SDBlock.ringPlatform;
							} else {
								b = Blocks.stained_hardened_clay;
								meta = 1;
							}
							world.setBlock(x + xo, y + yo, z + zo, b, meta, 2);
						}
					} else if (yo == 6) {
						// Ceiling
						world.setBlock(x + xo, y + yo, z + zo, Blocks.stained_hardened_clay, 1, 2);
					} else {
						world.setBlockToAir(x + xo, y + yo, z + zo);
					}
				}
			}
		}
		for (int yo = 3; yo < 6; yo++) {
			for (int zo = -1; zo < 3; zo++) {
					world.setBlock(x + 3, y + yo, z + zo, Blocks.stained_hardened_clay, 1, 2);
					world.setBlock(x - 3, y + yo, z + zo, Blocks.stained_hardened_clay, 1, 2);
			}
			for (int xo = -2; xo < 3; xo++) {
				// Redstone lamp
				if (xo == 0 && yo == 4) {
					world.setBlock(x + xo, y + yo, z - 2, Blocks.redstone_lamp);
					world.setBlock(x + xo, y + yo, z - 3, Blocks.lever, 12, 3);
					world.setBlock(x - xo, y + yo, z + 3, Blocks.redstone_lamp);
					world.setBlock(x - xo, y + yo, z + 4, Blocks.lever, 11, 3);
				} else {
					world.setBlock(x + xo, y + yo, z - 2, Blocks.stained_hardened_clay, 1, 2);
					world.setBlock(x + xo, y + yo, z + 3, Blocks.stained_hardened_clay, 1, 2);
				}
			}
		}
	}
}