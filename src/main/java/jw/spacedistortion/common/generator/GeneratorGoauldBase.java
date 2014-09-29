package jw.spacedistortion.common.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import jw.spacedistortion.common.SpaceDistortion;
import jw.spacedistortion.common.block.SDBlock;
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
		
		public GoauldRoom(int x, int z, ForgeDirection[] connections) {
			this.x = x;
			this.z = z;
			
			this.connections = new HashMap<ForgeDirection, Boolean>();
			for (ForgeDirection d : connections) this.connections.put(d, true);
			for (ForgeDirection d : new ForgeDirection[] { ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST })
				if (!this.connections.containsKey(d)) this.connections.put(d, false);
		}
		
		public int widthHeight() { return 9; }
		public int blockX() { return this.x * this.widthHeight(); }
		public int blockZ() { return this.z * this.widthHeight(); }
		
		public abstract void buildInWorld(World world, int blockOriginX, int blockOriginY, int blockOriginZ);
		
		protected int getbx(ForgeDirection direction, int sidewaysDistance, int forwardDistance) {
			if (direction == ForgeDirection.NORTH || direction == ForgeDirection.SOUTH) {
				return sidewaysDistance;
			} else {
				return forwardDistance * direction.offsetX;
			}
		}
		
		protected int getbz(ForgeDirection direction, int a, int factor) {
			if (direction == ForgeDirection.NORTH || direction == ForgeDirection.SOUTH) {
				return factor * direction.offsetZ;
			} else {
				return a;
			}
		}
	}
	
	public class GoauldCorridor extends GoauldRoom {
		public GoauldCorridor(int x, int z, HashMap<ForgeDirection, Boolean> connections) { super(x, z, connections); }
		public GoauldCorridor(int x, int z, ForgeDirection[] connections) { super(x, z, connections); }

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
					for (int y = -2; y < 1; y++) {
						world.setBlockToAir(xo + x, yo + y, zo + z);
					}
				}
			}
			// Center floor and ceiling
			for (int x = -1; x < 2; x++) {
				for (int z = -1; z < 2; z++) {
					world.setBlock(xo + x, yo - 3, zo + z, Blocks.sandstone);
					world.setBlock(xo + x, yo + 1, zo + z, Blocks.stained_hardened_clay, 1, 2);
				}
			}
			// Inner carpet
			world.setBlock(xo, yo - this.widthHeight() / 2 + 2, zo, Blocks.carpet, 4, 2);
			// Inner corners
			for (int x : new int[] {-2, 2}) {
				for (int z : new int[] {-2, 2}) {
					for (int y = -2; y < 1; y++) {
						world.setBlock(xo + x, yo + y, zo + z, Blocks.stained_hardened_clay, 1, 2);
					}
				}
			}
			// Torches
			for (int x : new int[] {-2, -1, 1, 2}) {
				for (int z : new int[] {-2, -1, 1, 2}) {
					if (Math.abs(x) + Math.abs(z) == 3) {
						world.setBlock(xo + x, yo - 1, zo + z, Blocks.torch);
					}
				}
			}
		}
		
		private void buildConnection(World world, int xo, int yo, int zo, ForgeDirection direction) {
			// Floor and ceiling
			for (int x = -1; x < 2; x++) {
				for (int z = -1; z < 2; z++) {
					world.setBlock(xo + x + (direction.offsetX * 3), yo - 3, zo + z + (direction.offsetZ * 3), Blocks.sandstone);
					world.setBlock(
							xo + x + (direction.offsetX * 3), yo + 1, zo + z + (direction.offsetZ * 3),
							Blocks.stained_hardened_clay, 1, 2);
				}
			}
			// Side wall
			for (int a : new int[] {-2, 2}) {
				for (int y = -2; y < 1; y++) {
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
				for (int y = -2; y < 1; y++) {
					world.setBlockToAir(xo + this.getbx(direction, s, 4), yo + y, zo + this.getbz(direction, s, 4));
				}
			}
			// Add carpet
			for (int f = 1; f < 5; f++) {
				world.setBlock(xo + this.getbx(direction, 0, f), yo - 2, zo + this.getbz(direction, 0, f), Blocks.carpet, 4, 2);
			}
		}
		
		private void buildEnd(World world, int xo, int yo, int zo, ForgeDirection direction) {
			for (int a = -1; a < 2; a++) {
				// Floor and ceiling portion
				for (int y : new int[] {-3, 1}) {
					world.setBlock(xo + this.getbx(direction, a, 2), yo + y, zo + this.getbz(direction, a, 2),
							Blocks.stained_hardened_clay, 1, 2);
				}
				// Back edge
				for (int y = -2; y < 1; y++) {
					world.setBlock(xo + this.getbx(direction, a, 3), yo + y, zo + this.getbz(direction, a, 3),
							Blocks.stained_hardened_clay, 1, 2);
				}
			}
			// Decoration blocks (walls are now completely sealed at this point)
			int xo_0_2 = xo + this.getbx(direction, 0, 2);
			int zo_0_2 = zo + this.getbz(direction, 0, 2);
			world.setBlock(xo_0_2, yo - 2, zo_0_2, Blocks.stained_hardened_clay, 1, 2);
			world.setBlock(xo_0_2, yo - 1, zo_0_2, Blocks.gold_block);
			world.setBlock(xo_0_2, yo, zo_0_2, Blocks.stained_hardened_clay, 1, 2);
			int xo_0_1 = xo + this.getbx(direction, 0, 1);
			int zo_0_1 = zo + this.getbz(direction, 0, 1);
			world.setBlock(xo_0_1, yo - 2, zo_0_1, Blocks.gold_block);
			world.setBlock(xo_0_1, yo, zo_0_1, Blocks.quartz_block);
		}
	}
	
	public class GoauldStargateRoom extends GoauldRoom {
		public GoauldStargateRoom(int x, int z, HashMap<ForgeDirection, Boolean> connections) { super(x, z, connections); }
		public GoauldStargateRoom(int x, int z, ForgeDirection[] connections) { super(x, z, connections); }

		@Override
		public void buildInWorld(World world, int blockOriginX, int blockOriginY, int blockOriginZ) {
			int xo = this.x + blockOriginX;
			int yo = blockOriginY;
			int zo = this.z + blockOriginZ;
			// First, build a big box and clear out the center
			for (int x = -4; x < 5; x++) {
				for (int y = -4; y < 5; y++) {
					for (int z = -4; z < 5; z++) {
						if (y == -4) {
							// Floor
							world.setBlock(xo + x, yo + y, zo + z, Blocks.sandstone);
						} else if (y == 4 || Math.abs(x) == 4 || Math.abs(z) == 4) {
							// Walls and ceiling
							world.setBlock(xo + x, yo + y, zo + z, Blocks.stained_hardened_clay, 1, 2);
						} else {
							// Inside space
							world.setBlockToAir(xo + x, yo + y, zo + z);
						}
					}
				}
			}
			boolean hasBuiltStargate = false;
			for (ForgeDirection d : new ForgeDirection[] { ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST }) {
				if (connections.get(d)) {
					this.buildEntrance(world, xo, yo, zo, d);
					if (!hasBuiltStargate) {
						Structure s = new Structure(xo + this.getbx(d, 3, -2), yo + 2, zo + this.getbz(d, 3, -2), d,
								SpaceDistortion.stargateRingShape, SpaceDistortion.templateBlockInfo, 0, 0);
						s.addToWorld(world);
						world.setBlock(xo + this.getbx(d, 1, -1), yo - 4, zo + this.getbz(d, 1, -1), SDBlock.stargateController,
								ForgeDirection.UP.ordinal(), 3);
						hasBuiltStargate = true;
					}
				}
			}
		}
		
		public void buildEntrance(World world, int xo, int yo, int zo, ForgeDirection direction) {
			// Carve the entrance hole
			for (int s = -1; s < 2; s++) {
				for (int y = -2; y < 1; y++) {
					world.setBlockToAir(xo + this.getbx(direction, s, 4), yo + y, zo + this.getbz(direction, s, 4));
				}
				world.setBlock(xo + this.getbx(direction, s, 4), yo - 3, zo + this.getbz(direction, s, 4), Blocks.stone_slab, 1, 2);
			}
		}
	}
	
	
	public GoauldRoom[] rooms;
	
	public GeneratorGoauldBase() { }
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (chunkX == 0 && chunkZ == 0) {
			this.generate(random, chunkX * 16, 100, chunkZ * 16, world);
		}
	}
	
	public void generate(Random random, int x, int y, int z, World world) {
		List<GoauldRoom> rooms = this.generateSchematic();
		this.buildSchematicInWorld(world, x, y, z, rooms);
	}
	
	public List<GoauldRoom> generateSchematic() {
		List<GoauldRoom> rooms = new ArrayList<GoauldRoom>();
		rooms.add(new GoauldStargateRoom(0, 0, new ForgeDirection[] { ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST }));
		HashMap<ForgeDirection, Boolean> connections = new HashMap<ForgeDirection, Boolean>();
		rooms.add(new GoauldCorridor(0, -1, new ForgeDirection[] { ForgeDirection.SOUTH }));
		rooms.add(new GoauldCorridor(0, 1, new ForgeDirection[] { ForgeDirection.NORTH }));
		rooms.add(new GoauldCorridor(1, 0, new ForgeDirection[] { ForgeDirection.WEST }));
		return rooms;
	}
	
	public HashMap<ForgeDirection, Boolean> presentKeysToMap(ForgeDirection[] keys) {
		HashMap<ForgeDirection, Boolean> result = new HashMap<ForgeDirection, Boolean>();
		for (ForgeDirection d : keys) result.put(d, true);
		for (ForgeDirection d : new ForgeDirection[] { ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST })
			if (!result.containsKey(d)) result.put(d, false);
		return result;
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
