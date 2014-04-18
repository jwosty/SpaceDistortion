package jw.spacedistortion.common.block;

import java.util.HashMap;

import jw.spacedistortion.Pair;
import jw.spacedistortion.StringGrid;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class Structure {
	public int x;
	public int y;
	public int z;
	public ForgeDirection facing;
	public HashMap<Pair<Integer, Integer>, BlockInfo> blocks;
	
	public static class BlockInfo {
		public int x;
		public int y;
		public int z;
		public Block blockType;
		
		public BlockInfo(int x, int y, int z, Block blockType) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.blockType = blockType;
		}
	}
	
	public Structure(int x, int y, int z, HashMap<Pair<Integer, Integer>, BlockInfo> blocks,
			ForgeDirection facing) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.blocks = blocks;
		this.facing = facing;
	}
	
	public static Structure detectStructure(IBlockAccess world, int x, int y, int z, StringGrid template, HashMap<Character, Block> charToBlock) {
		for (int xTemplateOffset = -template.width; xTemplateOffset <= template.width; xTemplateOffset++) {
			for (int yTemplateOffset = -template.height; yTemplateOffset <= template.height; yTemplateOffset++) {
				Structure s = Structure.detectStructureAtLocation(world, x + xTemplateOffset, y - yTemplateOffset, z, template, charToBlock);
				if (s != null) {
					return s;
				}
			}
		}
		return null;
	}
	
	public static Structure detectStructureAtLocation(IBlockAccess world, int x, int y, int z, StringGrid template,
			HashMap<Character, Block> charToBlock) {
		Structure result = new Structure(x, y, z, new HashMap<Pair<Integer, Integer>, BlockInfo>(), ForgeDirection.SOUTH);
		
		templateLoop: for (int tx = 0; tx <= template.width; tx++) {
			for (int ty = 0; ty <= template.height; ty++) {
				char templateChar = template.get(tx, ty);
				int bx = x + tx;
				int by = y - ty;
				int bz = z;
				Block worldBlock = world.getBlock(bx, by, bz);
				
				if (charToBlock.containsKey(templateChar) & worldBlock != charToBlock.get(templateChar)) {
					result = null;
					break templateLoop;
				} else {
					result.blocks.put(new Pair(tx, ty), new BlockInfo(bx, by, bz, worldBlock));
				}
			}
		}
		
		return result;
	}
	
	public static Structure detectConnectedStructure(IBlockAccess world, int x, int y, int z, StringGrid template,
			HashMap<Character, Block> charToBlock) {
		for (ForgeDirection d : ForgeDirection.VALID_DIRECTIONS) {
			int nx = x + d.offsetX;
			int ny = y + d.offsetY;
			int nz = z + d.offsetZ;
			Block neighbor = world.getBlock(nx, ny, nz);
			if (neighbor == SDBlock.stargateRing | neighbor == SDBlock.stargateRingChevron) {
				Structure stargate = Structure.detectStructure(world, nx, ny, nz, template, charToBlock);
				if (stargate != null) {
					return stargate;
				}
			}
		}
		return null;
	}
}
