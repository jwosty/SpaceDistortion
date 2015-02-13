package jw.spacedistortion.common.generator;

import java.util.Random;

import jw.spacedistortion.common.block.SDBlock;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import cpw.mods.fml.common.IWorldGenerator;

public class GeneratorOre implements IWorldGenerator {
	public WorldGenMinable gen;
	
	public GeneratorOre() { this.gen = new WorldGenMinable(SDBlock.naquadahOre, 6); }
	
	@Override
	public void generate(Random random, int chunkX, int chunkZ, World world,
			IChunkProvider chunkGenerator, IChunkProvider chunkProvider) {
		if (world.provider.dimensionId == 0) {
			for (int i = 0; i < 10; i++) {
				int x = (chunkX >> 4) + random.nextInt(16);
				int y = random.nextInt(25);
				int z = (chunkZ >> 4) + random.nextInt(16);
				this.gen.generate(world, random, x, y, z);
			}
		}
	}
}
