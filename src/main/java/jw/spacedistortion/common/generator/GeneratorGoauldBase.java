package jw.spacedistortion.common.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

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
			int max = this.widthHeight() / 2;
			int min = max - this.widthHeight();
			for (int x = min; x < max; x++) {
				for (int y = min; y < max; y++) {
					for (int z = min; z < max; z++) {
						if (x == min || x == max - 1 || y == min || y == max - 1 || z == min || z == max - 1) {
							int bx = blockOriginX + this.blockX() + x;
							int by = blockOriginY + y;
							int bz = blockOriginZ + this.blockZ() + z;
							world.setBlock(blockOriginX + this.blockX() + x, blockOriginY + y, blockOriginZ + this.blockZ() + z,
									Blocks.iron_block);
						}
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
			List<GoauldRoom> rooms = this.generateScematic();
			this.buildSchematicInWorld(world, chunkX * 16, 100, chunkZ * 16, rooms);
		}
	}

	public List<GoauldRoom> generateScematic() {
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
