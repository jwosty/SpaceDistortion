package jw.spacedistortion.common.generator.goauldbase;

import jw.spacedistortion.common.SpaceDistortion;
import jw.spacedistortion.common.block.SDBlock;
import jw.spacedistortion.common.block.Structure;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class GoauldStargateRoom extends GoauldRoom {
	public GoauldStargateRoom() { super(); }
	public GoauldStargateRoom(boolean[] connections) { super(connections); }

	@Override
	public void buildInWorld(World world, int x, int y, int z) {
		// First, build a big box and clear out the center
		for (int xo = -4; xo < 5; xo++) {
			for (int yo = -4; yo < 5; yo++) {
				for (int zo = -4; zo < 5; zo++) {
					if (yo == -4) {
						// Floor
						world.setBlock(x + xo, y + yo, z + zo, Blocks.sandstone);
					} else if (yo == 4 || Math.abs(xo) == 4 || Math.abs(zo) == 4) {
						// Walls and ceiling
						world.setBlock(x + xo, y + yo, z + zo, Blocks.stained_hardened_clay, 1, 2);
					} else {
						// Inside space
						world.setBlockToAir(x + xo, y + yo, z + zo);
					}
				}
			}
		}
		boolean hasBuiltStargate = false;
		for (int i = 0; i < this.connections.length; i++) {
			if (connections[i]) {
				ForgeDirection d = ForgeDirection.getOrientation(i + 2);
				// Cut out an entrance
				this.buildEntrance(world, x, y, z, d);
				if (!hasBuiltStargate) {
					// Add the stargate
					int sign = (d == ForgeDirection.NORTH || d == ForgeDirection.EAST) ? 1 : -1; 
					Structure s = new Structure(
							x + this.getbx(d, 3 * sign, -2), y + 2, z + this.getbz(d, 3 * sign, -2), d,
							SpaceDistortion.stargateRingShape, SpaceDistortion.templateBlockInfo, 0, 0);
					s.addToWorld(world);
					world.setBlock(x + this.getbx(d, sign, -1), y - 4, z + this.getbz(d, sign, -1), SDBlock.stargateController,
							ForgeDirection.UP.ordinal(), 3);
					hasBuiltStargate = true;
				}
			}
		}
		// Add torches
		for (int xo : new int[] {-3,-2,2,3}) {
			for (int zo : new int[] {-3,-2,2,3}) {
				if (Math.abs(xo) != Math.abs(zo) && world.getBlock(x + xo, y - 1, z + zo) == Blocks.air) {
					world.setBlock(x + xo, y - 1, z + zo, Blocks.torch);
				}
			}
		}
	}
	
	public void buildEntrance(World world, int x, int y, int z, ForgeDirection direction) {
		// Carve the entrance hole
		for (int s = -1; s < 2; s++) {
			for (int yo = -2; yo < 1; yo++) {
				world.setBlockToAir(x + this.getbx(direction, s, 4), y + yo, z + this.getbz(direction, s, 4));
			}
			world.setBlock(x + this.getbx(direction, s, 4), y - 3, z + this.getbz(direction, s, 4), Blocks.stone_slab, 1, 2);
		}
	}
}