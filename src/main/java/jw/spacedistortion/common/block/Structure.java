package jw.spacedistortion.common.block;

import java.util.HashMap;
import java.util.Map.Entry;

import jw.spacedistortion.Pair;
import jw.spacedistortion.StringGrid;
import jw.spacedistortion.Triplet;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
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
		
		protected BlockInfo() { };
		
		public BlockInfo(int x, int y, int z, Block blockType) {
			this.x = x;
			this.y = y;
			this.z = z;
			this.blockType = blockType;
		}
		
		public void writeToNBT(NBTTagCompound tag) {
			tag.setBoolean("isDirectional", false);
			tag.setInteger("x", this.x);
			tag.setInteger("y", this.y);
			tag.setInteger("z", this.z);
			tag.setInteger("b", Block.getIdFromBlock(this.blockType));
		}
		
		protected void readFromNBT(NBTTagCompound tag) {
			this.x = tag.getInteger("x");
			this.y = tag.getInteger("y");
			this.z = tag.getInteger("z");
			this.blockType = Block.getBlockById(tag.getInteger("b"));
		}
		
		public static BlockInfo createFromNBT(NBTTagCompound tag) {
			BlockInfo blockInfo = tag.getBoolean("isDirectional") ? new DirectionalBlockInfo() : new BlockInfo();
			blockInfo.readFromNBT(tag);
			return blockInfo;
		}
	}
	
	public static class DirectionalBlockInfo extends BlockInfo {
		public ForgeDirection direction;
		
		protected DirectionalBlockInfo() { }
		
		public DirectionalBlockInfo(int x, int y, int z, ForgeDirection direction, Block blockType) {
			super(x, y, z, blockType);
			this.direction = direction;
		}
		
		@Override
		public void writeToNBT(NBTTagCompound tag) {
			super.writeToNBT(tag);
			tag.setBoolean("isDirectional", true);
			tag.setByte("d", (byte)this.direction.ordinal());
		}
		
		@Override
		protected void readFromNBT(NBTTagCompound tag) {
			super.readFromNBT(tag);
			this.direction = ForgeDirection.getOrientation(tag.getByte("d"));
		}
	}
	
	protected Structure() { }
	
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
			HashMap<Character, Pair<Block, Boolean>> charToBlockAndHasDirection) {
		for (ForgeDirection d : ForgeDirection.VALID_DIRECTIONS) {
			int nx = x + d.offsetX;
			int ny = y + d.offsetY;
			int nz = z + d.offsetZ;
			Block neighbor = world.getBlock(nx, ny, nz);
			if (neighbor == SDBlock.stargateRing | neighbor == SDBlock.stargateRingChevron) {
				Structure stargate = Structure.detectStructure(world, nx, ny, nz, template, charToBlockAndHasDirection);
				if (stargate != null) {
					return stargate;
				}
			}
		}
		return null;
	}
	
	/** Detect a structure with an unknown offset and facing */
	public static Structure detectStructure(IBlockAccess world, int x, int y, int z, StringGrid template,
			HashMap<Character, Pair<Block, Boolean>> charToBlockAndHasDirection) {
		for (ForgeDirection facing : ForgeDirection.VALID_DIRECTIONS) {
			Structure s = Structure.detectStructure(world, x, y, z, template, charToBlockAndHasDirection, facing);
			if (s != null) {
				return s;
			}
		}
		return null;
	}
	
	/** Detect a structure with a known facing and unknown offset */
	public static Structure detectStructure(IBlockAccess world, int x, int y, int z, StringGrid template,
			HashMap<Character, Pair<Block, Boolean>> charToBlockAndHasDirection, ForgeDirection facing) {
		for (int xTemplateOffset = -template.width; xTemplateOffset <= template.width; xTemplateOffset++) {
			for (int yTemplateOffset = -template.height; yTemplateOffset <= template.height; yTemplateOffset++) {
				Triplet<Integer, Integer, Integer> offsetFromFacing = Structure.templateToWorldCoordinates(xTemplateOffset, yTemplateOffset, facing);
				Structure s = Structure.detectStructure(world,
						x + offsetFromFacing.X, y + offsetFromFacing.Y, z + offsetFromFacing.Z,
						facing, template, charToBlockAndHasDirection);
				if (s != null) {
					return s;
				}
			}
		}
		return null;
	}
	
	/** Detect a structure at the given concrete location (known offset) and facing */
	public static Structure detectStructure(IBlockAccess world, int x, int y, int z, ForgeDirection facing, StringGrid template,
			HashMap<Character, Pair<Block, Boolean>> charToBlockAndHasDirection) {
		Structure result = new Structure(x, y, z, new HashMap<Pair<Integer, Integer>, BlockInfo>(), facing);
		
		templateLoop: for (int tx = 0; tx <= template.width; tx++) {
			for (int ty = 0; ty <= template.height; ty++) {
				char templateChar = template.get(tx, ty);
				Triplet<Integer, Integer, Integer> offsetFromFacing = Structure.templateToWorldCoordinates(tx, ty, facing);
				int bx = x + offsetFromFacing.X;
				int by = y + offsetFromFacing.Y;
				int bz = z + offsetFromFacing.Z;
				Block worldBlock = world.getBlock(bx, by, bz);
				
				BlockInfo blockInfo;
				
				if (charToBlockAndHasDirection.containsKey(templateChar)) {
					int metadata = world.getBlockMetadata(bx, by, bz);
					
					Pair<Block, Boolean> blockAndHasDirection = charToBlockAndHasDirection.get(templateChar);
					Block block = blockAndHasDirection.X;
					boolean hasDirection = blockAndHasDirection.Y;
					
					// TODO: structure this better if possible; it's pretty ugly
					if (worldBlock == block) {
						if (hasDirection) {
							if (metadata == facing.ordinal()) {
								blockInfo = new DirectionalBlockInfo(bx, by, bz, facing, worldBlock);
							} else {
								result = null;
								break templateLoop;
							}
						} else {
							blockInfo = new BlockInfo(bx, by, bz, worldBlock);
						}
					} else {
						// Block didn't match
						result = null;
						break templateLoop;
					}
				} else {
					// Wildcard
					blockInfo = new BlockInfo(bx, by, bz, worldBlock);
				}
				
				// Getting this far means that the block matches the template
				result.blocks.put(new Pair<Integer, Integer>(tx, ty), blockInfo);
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
			y = -ty;
			break;
		case SOUTH:
			x = tx;
			y = -ty;
			break;
		case EAST:
			z = -tx;
			y = -ty;
			break;
		case WEST:
			z = tx;
			y = -ty;
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

	public void writeToNBT(NBTTagCompound tag) {
		tag.setInteger("x", this.x);
		tag.setInteger("y", this.y);
		tag.setInteger("z", this.z);
		tag.setByte("facing", (byte)this.facing.ordinal());
		NBTTagList blockInfoTags = new NBTTagList();
		for (Entry<Pair<Integer, Integer>, BlockInfo>entry : this.blocks.entrySet()) {
			NBTTagCompound blockInfoTag = new NBTTagCompound();
			blockInfoTag.setInteger("templateX", entry.getKey().X);
			blockInfoTag.setInteger("templateY", entry.getKey().Y);
			entry.getValue().writeToNBT(blockInfoTag);
		}
		tag.setTag("blocks", blockInfoTags);
	}
	
	private void readFromNBT(NBTTagCompound tag) {
		this.x = tag.getInteger("structureX");
		this.y = tag.getInteger("structureY");
		this.z = tag.getInteger("structureZ");
		this.facing = ForgeDirection.getOrientation(tag.getInteger("structureF"));
		this.blocks = new HashMap<Pair<Integer, Integer>, BlockInfo>();
		
		NBTTagList blockInfoTags = tag.getTagList("blocks", 10);
		for (int i = 0; i < blockInfoTags.tagCount(); i++) {
			NBTTagCompound blockInfoTag = blockInfoTags.getCompoundTagAt(i);
			this.blocks.put(
					new Pair<Integer, Integer>(blockInfoTag.getInteger("templateX"), blockInfoTag.getInteger("templateY")),
					BlockInfo.createFromNBT(blockInfoTag));
		}
	}
	
	/** Loads a structure from a NBT tag */
	public static Structure createFromNBT(NBTTagCompound tag) {
		Structure s = new Structure();
		s.readFromNBT(tag);
		return s;
	}
}
