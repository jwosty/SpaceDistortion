package jw.spacedistortion.common;

import java.util.Random;

import jw.spacedistortion.common.block.SDBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import cpw.mods.fml.common.IWorldGenerator;

public class OreGenerator implements IWorldGenerator {
	
	public OreGenerator() {}

	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		// only generate in the overworld
		if (world.provider.dimensionId == 0) {
			for (int i = 0; i < 10; i++) {
				int x = (chunkX * 16) + random.nextInt(16);
				int y = random.nextInt(25);
				int z = (chunkZ * 16) + random.nextInt(16);
				(new WorldGenMinable(SDBlock.naquadahOre, 6)).generate(world, random, x, y, z);
				System.out.println("Generated naquadah vein at (" + x + ", " + y + ", " + z + ")");
			}
		}
	}
}
