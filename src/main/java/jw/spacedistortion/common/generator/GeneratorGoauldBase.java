package jw.spacedistortion.common.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jw.spacedistortion.Pair;
import jw.spacedistortion.Triplet;
import jw.spacedistortion.common.SpaceDistortion;
import jw.spacedistortion.common.block.SDBlock;
import jw.spacedistortion.common.block.Structure;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.common.IWorldGenerator;

public class GeneratorGoauldBase implements IWorldGenerator {
	public static ForgeDirection[] possibleConnections = new ForgeDirection[] {
		ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST };
	
	public abstract class GoauldRoom {
		public HashMap<ForgeDirection, Boolean> connections;
		
		public GoauldRoom(HashMap<ForgeDirection, Boolean> connections) {
			for (ForgeDirection d : possibleConnections) {
				if (!connections.containsKey(d)) connections.put(d, false);
			}
			this.connections = connections;
		}
		
		public GoauldRoom(ForgeDirection[] connections) {
			this.connections = new HashMap<ForgeDirection, Boolean>();
			for (ForgeDirection d : connections) this.connections.put(d, true);
			for (ForgeDirection d : possibleConnections)
				if (!this.connections.containsKey(d)) this.connections.put(d, false);
		}
		
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
		public GoauldCorridor(HashMap<ForgeDirection, Boolean> connections) { super(connections); }
		public GoauldCorridor(ForgeDirection[] connections) { super(connections); }

		@Override
		public void buildInWorld(World world, int x, int y, int z) {
			this.buildCenter(world, x, y, z);
			for (ForgeDirection d : possibleConnections) {
				if (this.connections.get(d)) {
					this.buildConnection(world, x, y, z, d);
				} else {
					this.buildEnd(world, x, y, z, d);
				}
			}
		}

		private void buildCenter(World world, int x, int y, int z) {
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
		
		private void buildConnection(World world, int x, int y, int z, ForgeDirection direction) {
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
		
		private void buildEnd(World world, int x, int y, int z, ForgeDirection direction) {
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
			world.setBlock(xo_0_1, y - 2, zo_0_1, Blocks.gold_block);
			world.setBlock(xo_0_1, y, zo_0_1, Blocks.quartz_block);
		}
	}
	
	public class GoauldStargateRoom extends GoauldRoom {
		public GoauldStargateRoom(HashMap<ForgeDirection, Boolean> connections) { super(connections); }
		public GoauldStargateRoom(ForgeDirection[] connections) { super(connections); }

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
			for (ForgeDirection d : possibleConnections) {
				if (connections.get(d)) {
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
	
	public GoauldRoom[] rooms;
	
	public GeneratorGoauldBase() { }
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (chunkX == 0 && chunkZ == 0) {
			this.generate(random, chunkX * 16, 20, chunkZ * 16, world);
		}
	}
	
	public void generate(Random random, int x, int y, int z, World world) {
		HashMap<Pair<Integer, Integer>, GoauldRoom> rooms = this.generateSchematic(random);
		this.buildSchematicInWorld(world, x, y, z, rooms);
	}
	
	public HashMap<Pair<Integer, Integer>, GoauldRoom> generateSchematic(Random random) {
		HashMap<Pair<Integer, Integer>, GoauldRoom> rooms = new HashMap<Pair<Integer, Integer>, GoauldRoom>();
		rooms.put(new Pair<Integer, Integer>(0, 0), new GoauldStargateRoom(new HashMap<ForgeDirection, Boolean>()));
		
		List<ForgeDirection> connections = this.generateConnections(random);
		List<Triplet<Integer, ForgeDirection, Integer>> growthPoints = new ArrayList<Triplet<Integer, ForgeDirection, Integer>>();
		growthPoints.add(new Triplet<Integer, ForgeDirection, Integer>(0, null, 0));
		for (int i = 0; i < 5; i++) {
			growthPoints = this.doSingleSchematicIteration(random, growthPoints, rooms, i >= 5);
		}
		return rooms;
	}
	
	// Performs one schematic iteration, adding rooms and returning the new growth points
	public List<Triplet<Integer, ForgeDirection, Integer>> doSingleSchematicIteration(
			Random random, List<Triplet<Integer, ForgeDirection, Integer>> growthPoints, HashMap<Pair<Integer, Integer>, GoauldRoom> rooms, boolean isLastIteration) {
		List<Triplet<Integer, ForgeDirection, Integer>> newGrowthPoints = new ArrayList<Triplet<Integer, ForgeDirection, Integer>>();
		for (Triplet<Integer, ForgeDirection, Integer> growth : growthPoints) {
			// Connect to the previous room
			HashMap<ForgeDirection, Boolean> connections = new HashMap<ForgeDirection, Boolean>();
			if (growth.Y != null) connections.put(growth.Y, true);
			GoauldRoom room = (growth.X == 0 && growth.Z == 0) ? new GoauldStargateRoom(connections) : new GoauldCorridor(connections);
			// Add more points for other rooms to generate from next iteration
			for (ForgeDirection c : this.generateConnections(random)) {
				if (!rooms.containsKey(new Pair<Integer, Integer>(growth.X + c.offsetX, growth.Z + c.offsetZ))) {
					room.connections.put(c, true);
					newGrowthPoints.add(new Triplet<Integer, ForgeDirection, Integer>(growth.X + c.offsetX, c.getOpposite(), growth.Z + c.offsetZ));
				}
			}
			// Finalize this room
			rooms.put(new Pair<Integer, Integer>(growth.X, growth.Z), room);
		}
		return newGrowthPoints;
	}
	
	// Get an integer that is 1 every 1/6 times, 2 every 2/3 times, and 3 every 1/6 times
	public int getWeightedInt(Random random) {
		int n = random.nextInt(7);
		if (n == 1) return 1;
		else if (n >= 2 && n <= 5) return 2;
		else return 3;
	}
	
	public List<ForgeDirection> generateConnections(Random random) {
		int nConnections = this.getWeightedInt(random);
		List<ForgeDirection> connections = new ArrayList<ForgeDirection>();
		List<ForgeDirection> possibleConnectionsL = new ArrayList<ForgeDirection>(Arrays.asList(possibleConnections));
		for (int times = 0; times < nConnections; times++) {
			int i = random.nextInt(possibleConnectionsL.size());
			connections.add(possibleConnectionsL.get(i));
			possibleConnectionsL.remove(i);
		}
		return connections;
	}
	
	public HashMap<ForgeDirection, Boolean> presentKeysToMap(ForgeDirection[] keys) {
		HashMap<ForgeDirection, Boolean> result = new HashMap<ForgeDirection, Boolean>();
		for (ForgeDirection d : keys) result.put(d, true);
		for (ForgeDirection d : new ForgeDirection[] { ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST })
			if (!result.containsKey(d)) result.put(d, false);
		return result;
	}
	
	public void buildSchematicInWorld(World world, int originX, int originY, int originZ, HashMap<Pair<Integer, Integer>, GoauldRoom> rooms) {
		for (Map.Entry<Pair<Integer, Integer>, GoauldRoom> entry : rooms.entrySet()) {
			Pair<Integer, Integer> roomCoords = entry.getKey();
			entry.getValue().buildInWorld(world, originX + (roomCoords.X * 9), originY, originZ + (roomCoords.Y * 9));
		}
	}
}
