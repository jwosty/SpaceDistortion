package jw.spacedistortion.common.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import jw.spacedistortion.Pair;
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
			// Center floor and ceiling
			for (int x = -1; x < 2; x++) {
				for (int z = -1; z < 2; z++) {
					world.setBlock(xo + x, yo - 4, zo + z, Blocks.sandstone);
					world.setBlock(xo + x, yo, zo + z, Blocks.stained_hardened_clay);
				}
			}
			world.setBlock(xo, yo - this.widthHeight() / 2 + 1, zo, Blocks.carpet);
			// Inner corners
			for (int x : new int[] {-2, 2}) {
				for (int z : new int[] {-2, 2}) {
					for (int y = -3; y < 0; y++) {
						world.setBlock(xo + x, yo + y, zo + z, Blocks.stained_hardened_clay);
					}
					
				}
			}
			for (int x : new int[] {-2, -1, 1, 2}) {
				for (int z : new int[] {-2, -1, 1, 2}) {
					if (Math.abs(x) + Math.abs(z) == 3) {
						world.setBlock(xo + x, yo - 2, zo + z, Blocks.torch);
					}
				}
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
		connections.put(ForgeDirection.SOUTH, true);
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
