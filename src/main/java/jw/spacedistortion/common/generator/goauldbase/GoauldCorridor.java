package jw.spacedistortion.common.generator.goauldbase;

import java.util.Random;

import jw.spacedistortion.common.SpaceDistortion;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.util.ForgeDirection;

public class GoauldCorridor extends GoauldRoom {
	public GoauldCorridor() { super(); }
	public GoauldCorridor(boolean[] connections) { super(connections); }

	@Override
	public void buildInWorld(Random rand, World world, int x, int y, int z) {
		this.buildCenter(rand, world, x, y, z);
		for (int i = 0; i < this.connections.length; i++) {
			if (this.connections[i]) {
				this.buildConnection(rand, world, x, y, z, ForgeDirection.getOrientation(i + 2));
			} else {
				this.buildEnd(rand, world, x, y, z, ForgeDirection.getOrientation(i + 2));
			}
		}
	}

	private void buildCenter(Random rand, World world, int x, int y, int z) {
		// Remove some blocks so torches place in the right directions
		for (int xo = -3; xo < 4; xo++) {
			for (int zo = -3; zo < 4; zo++) {
				for (int yo = -2; yo < 1; yo++) {
					world.setBlockToAir(x + xo, y + yo, z + zo);
				}
			}
		}
		// Center floor and ceiling
		for (int xo = -1; xo < 2; xo++) {
			for (int zo = -1; zo < 2; zo++) {
				world.setBlock(x + xo, y - 3, z + zo, Blocks.sandstone);
				world.setBlock(x + xo, y + 1, z + zo, Blocks.stained_hardened_clay, 1, 2);
			}
		}
		// Inner carpet
		world.setBlock(x, y - 9 / 2 + 2, z, Blocks.carpet, 4, 2);
		// Inner corners
		for (int xo : new int[] {-2, 2}) {
			for (int zo : new int[] {-2, 2}) {
				for (int yo = -2; yo < 1; yo++) {
					world.setBlock(x + xo, y + yo, z + zo, Blocks.stained_hardened_clay, 1, 2);
				}
			}
		}
		// Torches
		for (int xo : new int[] {-2, -1, 1, 2}) {
			for (int zo : new int[] {-2, -1, 1, 2}) {
				if (Math.abs(xo) + Math.abs(zo) == 3) {
					world.setBlock(x + xo, y - 1, z + zo, Blocks.torch);
				}
			}
		}
	}
	
	private void buildConnection(Random rand, World world, int x, int y, int z, ForgeDirection direction) {
		// Floor and ceiling
		for (int xo = -1; xo < 2; xo++) {
			for (int zo = -1; zo < 2; zo++) {
				world.setBlock(x + xo + (direction.offsetX * 3), y - 3, z + zo + (direction.offsetZ * 3), Blocks.sandstone);
				world.setBlock(
						x + xo + (direction.offsetX * 3), y + 1, z + zo + (direction.offsetZ * 3),
						Blocks.stained_hardened_clay, 1, 2);
			}
		}
		// Side wall
		for (int a : new int[] {-2, 2}) {
			for (int yo = -2; yo < 1; yo++) {
				for (int b : new int[] {3, 4}) {
					int xo;
					int zo;
					if (direction == ForgeDirection.NORTH || direction == ForgeDirection.SOUTH) {
						xo = a;
						zo = b * direction.offsetZ;
					} else {
						xo = b * direction.offsetX;
						zo = a;
					}
					world.setBlock(x + xo, y + yo, z + zo, Blocks.stained_hardened_clay, 1, 2);
				}
			}
		}
		// Remove any remaining blocks inside the corridor
		for (int s = -1; s < 2; s++) {
			for (int yo = -2; yo < 1; yo++) {
				world.setBlockToAir(x + this.getbx(direction, s, 4), y + yo, z + this.getbz(direction, s, 4));
			}
		}
		// Add carpet
		for (int f = 1; f < 5; f++) {
			world.setBlock(x + this.getbx(direction, 0, f), y - 2, z + this.getbz(direction, 0, f), Blocks.carpet, 4, 2);
		}
	}
	
	private void buildEnd(Random rand, World world, int x, int y, int z, ForgeDirection direction) {
		for (int a = -1; a < 2; a++) {
			// Floor and ceiling portion
			for (int yo : new int[] {-3, 1}) {
				world.setBlock(x + this.getbx(direction, a, 2), y + yo, z + this.getbz(direction, a, 2),
						Blocks.stained_hardened_clay, 1, 2);
			}
			// Back edge
			for (int yo = -2; yo < 1; yo++) {
				world.setBlock(x + this.getbx(direction, a, 3), y + yo, z + this.getbz(direction, a, 3),
						Blocks.stained_hardened_clay, 1, 2);
			}
		}
		// Decoration blocks (walls are now completely sealed at this point)
		int xo_0_2 = x + this.getbx(direction, 0, 2);
		int zo_0_2 = z + this.getbz(direction, 0, 2);
		world.setBlock(xo_0_2, y - 2, zo_0_2, Blocks.stained_hardened_clay, 1, 2);
		world.setBlock(xo_0_2, y - 1, zo_0_2, Blocks.gold_block);
		world.setBlock(xo_0_2, y, zo_0_2, Blocks.stained_hardened_clay, 1, 2);
		int xo_0_1 = x + this.getbx(direction, 0, 1);
		int zo_0_1 = z + this.getbz(direction, 0, 1);
		if (rand.nextInt(16) == 0) {
			world.setBlock(xo_0_1, y - 2, zo_0_1, Blocks.chest, 0, 2);
			TileEntityChest chest = (TileEntityChest)world.getTileEntity(xo_0_1, y - 2, zo_0_1);
			ChestGenHooks info = ChestGenHooks.getInfo(SpaceDistortion.genGoauldCorridor);
			int count = info.getCount(rand);
			WeightedRandomChestContent.generateChestContents(rand, info.getItems(rand), chest, count);
			int foo = 42;
		} else {
			world.setBlock(xo_0_1, y - 2, zo_0_1, Blocks.gold_block);
		}
		world.setBlock(xo_0_1, y, zo_0_1, Blocks.quartz_block);
	}
}