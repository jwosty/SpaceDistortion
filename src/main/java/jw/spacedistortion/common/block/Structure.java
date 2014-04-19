package jw.spacedistortion.common.block;

import java.util.HashMap;

import jw.spacedistortion.Pair;
import jw.spacedistortion.StringGrid;
import jw.spacedistortion.Triplet;
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
	
	/** Attempt to find a structure attached to the given block */
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
	
	/** Detect a structure with an unknown offset and facing */
	public static Structure detectStructure(IBlockAccess world, int x, int y, int z, StringGrid template,
			HashMap<Character, Block> charToBlock) {
		//ForgeDirection facing = ForgeDirection.NORTH;
		for (ForgeDirection facing : ForgeDirection.VALID_DIRECTIONS) {
			for (int xTemplateOffset = -template.width; xTemplateOffset <= template.width; xTemplateOffset++) {
				for (int yTemplateOffset = -template.height; yTemplateOffset <= template.height; yTemplateOffset++) {
					Triplet<Integer, Integer, Integer> offsetFromFacing = Structure.templateToWorldCoordinates(xTemplateOffset, yTemplateOffset, facing);
					Structure s = Structure.detectStructureAtLocationAndOrientation(world,
							x + offsetFromFacing.X, y + offsetFromFacing.Y, z + offsetFromFacing.Z,
							facing, template, charToBlock);
					if (s != null) {
						return s;
					}
				}
			}
		}
		return null;
	}
	
	/** Detect a structure at the given concrete location (known offset) and facing */
	public static Structure detectStructureAtLocationAndOrientation(IBlockAccess world, int x, int y, int z, ForgeDirection facing, StringGrid template,
			HashMap<Character, Block> charToBlock) {
		Structure result = new Structure(x, y, z, new HashMap<Pair<Integer, Integer>, BlockInfo>(), facing);
		
		templateLoop: for (int tx = 0; tx <= template.width; tx++) {
			for (int ty = 0; ty <= template.height; ty++) {
				char templateChar = template.get(tx, ty);
				Triplet<Integer, Integer, Integer> offsetFromFacing = Structure.templateToWorldCoordinates(tx, ty, facing);
				int bx = x + offsetFromFacing.X;
				int by = y + offsetFromFacing.Y;
				int bz = z + offsetFromFacing.Z;
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
	
	public static Triplet<Integer, Integer, Integer> templateToWorldCoordinates(int tx, int ty, ForgeDirection facing) {
		int x = 0;
		int y = 0;
		int z = 0;
		
		switch (facing) {
		case NORTH:
			x = -tx;
			y = ty;
			break;
		case SOUTH:
			x = tx;
			y = ty;
			break;
		case EAST:
			z = tx;
			y = ty;
			break;
		case WEST:
			z = -tx;
			y = ty;
			break;
		case UP:
			x = tx;
			z = ty;
			break;
		case DOWN:
			x = -tx;
			z = ty;
			break;
		case UNKNOWN:
			throw new RuntimeException("Unknown Forge direction");
		}
		
		return new Triplet<Integer, Integer, Integer>(x, y, z);
	}
}
