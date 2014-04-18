package jw.spacedistortion.common.block;

import java.util.HashMap;

import jw.spacedistortion.Pair;
import jw.spacedistortion.StringGrid;
import jw.spacedistortion.Triplet;
import net.minecraft.block.Block;
import net.minecraft.world.IBlockAccess;
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
	
	public static Structure detectStructure(IBlockAccess world, int x, int y, int z, StringGrid template,
			HashMap<Character, Block> charToBlock) {
		HashMap<Pair<Integer, Integer>, BlockInfo> blocks =
				new HashMap<Pair<Integer, Integer>, BlockInfo>();
		Structure result = new Structure(x, y, z, new HashMap<Pair<Integer, Integer>, BlockInfo>(), ForgeDirection.SOUTH);
		
		templateLoop: for (int tx = -template.width; tx <= template.width; tx++) {
			for (int ty = -template.height; ty <= template.height; ty++) {
				char templateChar = template.get(tx, ty);
				int bx = x + tx;
				int by = y + ty;
				int bz = z;
				Block worldBlock = world.getBlock(bx, by, bz);
				int worldBlockMetadata = world.getBlockMetadata(x + tx, y + ty, z);
				
				if (charToBlock.containsKey(templateChar) & worldBlock == charToBlock.get(templateChar)) {
					result.blocks.put(new Pair(tx, ty), new BlockInfo(bx, by, bz, worldBlock));
				} else {
					result = null;
					break templateLoop;
				}
			}
		}
		
		return result;
	}
}
