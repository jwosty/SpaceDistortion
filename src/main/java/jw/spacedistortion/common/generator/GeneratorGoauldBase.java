package jw.spacedistortion.common.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import jw.spacedistortion.Triplet;
import jw.spacedistortion.common.block.Structure;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.IWorldGenerator;

public class GeneratorGoauldBase implements IWorldGenerator {
	public abstract class GoauldRoom {
		// X position (in terms of rooms, not blocks)
		public int x;
		// Y position (in terms of rooms, not blocks)
		public int z;
		public HashMap<ForgeDirection, Boolean> connections;
		public GoauldRoom(int x, int z, HashMap<ForgeDirection, Boolean> connections) {
			this.x = x;
			this.z = z;
			for (ForgeDirection d : new ForgeDirection[] { ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST }) {
				if (!connections.containsKey(d)) connections.put(d, false);
			}
			this.connections = connections;
		}
		
		public int widthHeight() { return 9; }
		public int blockX() { return this.x * this.widthHeight(); }
		public int blockZ() { return this.z * this.widthHeight(); }
		
		public abstract void buildInWorld(World world, int blockOriginX, int blockOriginY, int blockOriginZ);
	}
	
	public class GoauldCorridor extends GoauldRoom {
		public GoauldCorridor(int x, int z, HashMap<ForgeDirection, Boolean> connections) { super(x, z, connections); }

		@Override
		public void buildInWorld(World world, int blockOriginX, int blockOriginY, int blockOriginZ) {
			int max = Math.round((float)this.widthHeight() / 2F);
			int min = max - this.widthHeight();
			int xo = blockOriginX + this.blockX();
			int yo = blockOriginY;
			int zo = blockOriginZ + this.blockZ();
			this.buildCenter(world, xo, yo, zo);
			for (ForgeDirection d : new ForgeDirection[] { ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST }) {
				if (this.connections.get(d)) {
					this.buildConnection(world, xo, yo, zo, d);
				} else {
					this.buildEnd(world, xo, yo, zo, d);
				}
			}
		}

		private void buildCenter(World world, int xo, int yo, int zo) {
			// Remove some blocks so torches place in the right directions
			for (int x = -3; x < 4; x++) {
				for (int z = -3; z < 4; z++) {
					for (int y = -3; y < 0; y++) {
						world.setBlockToAir(xo + x, yo + y, zo + z);
					}
				}
			}
			// Center floor and ceiling
			for (int x = -1; x < 2; x++) {
				for (int z = -1; z < 2; z++) {
					world.setBlock(xo + x, yo - 4, zo + z, Blocks.sandstone);
					world.setBlock(xo + x, yo, zo + z, Blocks.stained_hardened_clay, 1, 2);
				}
			}
			world.setBlock(xo, yo - this.widthHeight() / 2 + 1, zo, Blocks.carpet, 4, 2);
			// Inner corners
			for (int x : new int[] {-2, 2}) {
				for (int z : new int[] {-2, 2}) {
					for (int y = -3; y < 0; y++) {
						world.setBlock(xo + x, yo + y, zo + z, Blocks.stained_hardened_clay, 1, 2);
					}
				}
			}
			// Torches
			for (int x : new int[] {-2, -1, 1, 2}) {
				for (int z : new int[] {-2, -1, 1, 2}) {
					if (Math.abs(x) + Math.abs(z) == 3) {
						world.setBlock(xo + x, yo - 2, zo + z, Blocks.torch);
					}
				}
			}
		}
		
		private void buildConnection(World world, int xo, int yo, int zo, ForgeDirection direction) {
			// Floor and ceiling
			for (int x = -1; x < 2; x++) {
				for (int z = -1; z < 2; z++) {
					world.setBlock(xo + x + (direction.offsetX * 3), yo - 4, zo + z + (direction.offsetZ * 3), Blocks.sandstone);
					world.setBlock(
							xo + x + (direction.offsetX * 3), yo, zo + z + (direction.offsetZ * 3),
							Blocks.stained_hardened_clay, 1, 2);
				}
			}
			// Side wall
			for (int a : new int[] {-2, 2}) {
				for (int y = -3; y < 0; y++) {
					for (int b : new int[] {3, 4}) {
						int x;
						int z;
						if (direction == ForgeDirection.NORTH || direction == ForgeDirection.SOUTH) {
							x = a;
							z = b * direction.offsetZ;
						} else {
							x = b * direction.offsetX;
							z = a;
						}
						world.setBlock(xo + x, yo + y, zo + z, Blocks.stained_hardened_clay, 1, 2);
					}
				}
			}
			// Remove any remaining blocks inside the corridor
			for (int s = -1; s < 2; s++) {
				for (int y = -3; y < 0; y++) {
					world.setBlockToAir(xo + this.getbx(direction, s, 4), yo + y, zo + this.getbz(direction, s, 4));
				}
			}
			// Add carpet
			for (int f = 1; f < 5; f++) {
				world.setBlock(xo + this.getbx(direction, 0, f), yo - 3, zo + this.getbz(direction, 0, f), Blocks.carpet, 4, 2);
			}
		}
		
		private void buildEnd(World world, int xo, int yo, int zo, ForgeDirection direction) {
			for (int a = -1; a < 2; a++) {
				// Floor and ceiling portion
				for (int y : new int[] {-4, 0}) {
					world.setBlock(xo + this.getbx(direction, a, 2), yo + y, zo + this.getbz(direction, a, 2),
							Blocks.stained_hardened_clay, 1, 2);
				}
				// Back edge
				for (int y = -3; y < 0; y++) {
					world.setBlock(xo + this.getbx(direction, a, 3), yo + y, zo + this.getbz(direction, a, 3),
							Blocks.stained_hardened_clay, 1, 2);
				}
			}
			// Decoration blocks (walls are now completely sealed at this point)
			int xo_0_2 = xo + this.getbx(direction, 0, 2);
			int zo_0_2 = zo + this.getbz(direction, 0, 2);
			world.setBlock(xo_0_2, yo - 3, zo_0_2, Blocks.stained_hardened_clay, 1, 2);
			world.setBlock(xo_0_2, yo - 2, zo_0_2, Blocks.gold_block);
			world.setBlock(xo_0_2, yo - 1, zo_0_2, Blocks.stained_hardened_clay, 1, 2);
			int xo_0_1 = xo + this.getbx(direction, 0, 1);
			int zo_0_1 = zo + this.getbz(direction, 0, 1);
			world.setBlock(xo_0_1, yo - 3, zo_0_1, Blocks.gold_block);
			world.setBlock(xo_0_1, yo - 1, zo_0_1, Blocks.quartz_block);
		}
		
		private int getbx(ForgeDirection direction, int sidewaysDistance, int forwardDistance) {
			if (direction == ForgeDirection.NORTH || direction == ForgeDirection.SOUTH) {
				return sidewaysDistance;
			} else {
				return forwardDistance * direction.offsetX;
			}
		}
		
		private int getbz(ForgeDirection direction, int a, int factor) {
			if (direction == ForgeDirection.NORTH || direction == ForgeDirection.SOUTH) {
				return factor * direction.offsetZ;
			} else {
				return a;
			}
		}
	}
	
	public class GoauldStargateRoom extends GoauldRoom {
		public GoauldStargateRoom(int x, int z, ForgeDirection connection) {
			super(x, z, null);
			HashMap<ForgeDirection, Boolean> connections = new HashMap<ForgeDirection, Boolean>();
			connections.put(connection, true);
			for (ForgeDirection d : ForgeDirection.VALID_DIRECTIONS) {
				if (d != connection) connections.put(d, false);
			}
		}

		@Override
		public void buildInWorld(World world, int blockOriginX, int blockOriginY, int blockOriginZ) {
		}
	}
	
	
	public GoauldRoom[] rooms;
	
	public GeneratorGoauldBase() { }
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (chunkX == 0 && chunkZ == 0) {
			this.generate(random, chunkX * 16, 100, chunkZ * 16, world);
			//List<GoauldRoom> rooms = this.generateSchematic();
			//this.buildSchematicInWorld(world, chunkX * 16, 100, chunkZ * 16, rooms);
		}
	}
	
	public void generate(Random random, int x, int y, int z, World world) {
		List<GoauldRoom> rooms = this.generateSchematic();
		this.buildSchematicInWorld(world, x, y, z, rooms);
	}

	public List<GoauldRoom> generateSchematic() {
		List<GoauldRoom> rooms = new ArrayList<GoauldRoom>();
		HashMap<ForgeDirection, Boolean> connections = new HashMap<ForgeDirection, Boolean>();
		connections.put(ForgeDirection.NORTH, true);
		connections.put(ForgeDirection.EAST, true);
		GoauldRoom start = new GoauldCorridor(0, 0, connections);
		rooms.add(start);
		return rooms;
	}
	
	public void generateSchematicBranch(Random random, int x, int y, int length, List<GoauldRoom> rooms) {
		if (length > 0) {
			
		}
	}
	
	public void buildSchematicInWorld(World world, int originX, int originY, int originZ, List<GoauldRoom> rooms) {
		for (GoauldRoom room : rooms) {
			room.buildInWorld(world, originX, originY, originZ);
		}
	}
}
