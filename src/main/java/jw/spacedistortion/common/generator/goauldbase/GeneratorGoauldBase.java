package jw.spacedistortion.common.generator.goauldbase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import jw.spacedistortion.common.EntitySpawnPreventer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraftforge.common.util.ForgeDirection;
import scala.Tuple2;
import scala.Tuple3;
import cpw.mods.fml.common.IWorldGenerator;

public class GeneratorGoauldBase implements IWorldGenerator {
	public static ForgeDirection[] possibleConnections = new ForgeDirection[] {
		ForgeDirection.NORTH, ForgeDirection.SOUTH, ForgeDirection.EAST, ForgeDirection.WEST };
	
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
		HashMap<Tuple2<Integer, Integer>, GoauldRoom> rooms = this.generateSchematic(random);
		this.buildSchematicInWorld(world, x, y, z, rooms);
	}
	
	public HashMap<Tuple2<Integer, Integer>, GoauldRoom> generateSchematic(Random random) {
		HashMap<Tuple2<Integer, Integer>, GoauldRoom> rooms = new HashMap<Tuple2<Integer, Integer>, GoauldRoom>();
		
		rooms.put(new Tuple2<Integer, Integer>(0, 0), new GoauldRingRoom(new boolean[]{ true, true, true, true }));
		
		/*
		List<Tuple3<Integer, ForgeDirection, Integer>> growthPoints = new ArrayList<Tuple3<Integer, ForgeDirection, Integer>>();
		growthPoints.add(new Tuple3<Integer, ForgeDirection, Integer>(0, null, 0));
		int depth = 8;
		for (int i = 0; i < depth; i++) {
			growthPoints = this.doSingleSchematicIteration(random, growthPoints, rooms, i >= (depth - 1));
		}
		*/
		return rooms;
	}
	
	// Performs one schematic iteration, adding rooms and returning the new growth points
	public List<Tuple3<Integer, ForgeDirection, Integer>> doSingleSchematicIteration(
			Random random, List<Tuple3<Integer, ForgeDirection, Integer>> growthPoints, HashMap<Tuple2<Integer, Integer>, GoauldRoom> rooms, boolean isLastIteration) {
		List<Tuple3<Integer, ForgeDirection, Integer>> newGrowthPoints = new ArrayList<Tuple3<Integer, ForgeDirection, Integer>>();
		for (Tuple3<Integer, ForgeDirection, Integer> growth : growthPoints) {
			Tuple2<Integer, Integer> roomPos = new Tuple2<Integer, Integer>(growth._1(), growth._3());
			boolean[] connections;
			GoauldRoom room;
			if (rooms.containsKey(roomPos)) {
				room = rooms.get(roomPos);
				connections = new boolean[0];
			} else {
				room = (growth._1() == 0 && growth._3() == 0) ? new GoauldStargateRoom() : new GoauldCorridor();
				rooms.put(roomPos, room);
				connections = this.generateConnections(random);
			}
			// Connect to the previous room
			if (growth._2() != null) room.setConnection(growth._2(), true);
			// Add more points for other rooms to generate from next iteration
			if (!isLastIteration) {
				for (int i = 0; i < connections.length; i++) {
					if (connections[i]) {
						ForgeDirection c = ForgeDirection.getOrientation(i + 2);
						if (growth._2() != null || growth._2() != c.getOpposite()) {
							Tuple2<Integer, Integer> pos = new Tuple2<Integer, Integer>(growth._1() + c.offsetX, growth._3() + c.offsetZ);
							// If there's already a room there, connect it 50% of the time
							if (rooms.containsKey(pos)) {
								if (random.nextInt() % 4 == 0) {
									rooms.get(pos).setConnection(c.getOpposite(), true);
									room.setConnection(c, true);
								}
							} else { 
							// Otherwise, add another growth point
								newGrowthPoints.add(new Tuple3<Integer, ForgeDirection, Integer>(pos._1(), c.getOpposite(), pos._2()));
								room.setConnection(c, true);
							}
						}
					}
				}
			}
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
	
	public boolean[] generateConnections(Random random) {
		int nConnections = this.getWeightedInt(random);
		boolean[] connections = new boolean[4];
		List<ForgeDirection> possibleConnectionsL = new ArrayList<ForgeDirection>(Arrays.asList(possibleConnections));
		for (int times = 0; times < nConnections; times++) {
			int i = random.nextInt(possibleConnectionsL.size());
			connections[possibleConnectionsL.get(i).ordinal() - 2] = true;
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
	
	public void buildSchematicInWorld(World world, int originX, int originY, int originZ, HashMap<Tuple2<Integer, Integer>, GoauldRoom> rooms) {
		EntitySpawnPreventer preventer = new EntitySpawnPreventer(true);
		preventer.filter = EntityItem.class;
		for (Map.Entry<Tuple2<Integer, Integer>, GoauldRoom> entry : rooms.entrySet()) {
			Tuple2<Integer, Integer> roomCoords = entry.getKey();
			entry.getValue().buildInWorld(world, originX + (roomCoords._1 * 9), originY, originZ + (roomCoords._2 * 9));
		}
		preventer.unregister();
	}
}
