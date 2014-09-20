package jw.spacedistortion.common.block;

import java.util.HashMap;

import jw.spacedistortion.Pair;
import jw.spacedistortion.client.ClientProxy;
import jw.spacedistortion.common.SpaceDistortion;
import jw.spacedistortion.common.entity.EntityTransporterRings;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockRingPlatform extends SDBlock {
	public BlockRingPlatform() {
		super(Material.rock);
	}
	
	//public HashMap<Pair<ForgeDirection, ForgeDirection>, IIcon> faceDirection = new HashMap<Pair<ForgeDirection, ForgeDirection>, IIcon>();
	
	public IIcon[] icons = new IIcon[6];
	
	public boolean directionsMatch(ForgeDirection a, ForgeDirection b, ForgeDirection expected1, ForgeDirection expected2) {
		return (a == expected1 && b == expected2) || (a == expected2 && b == expected1);
	}
	
	public int getIndexFromOrientation(ForgeDirection a, ForgeDirection b) {
		if (directionsMatch(a, b, ForgeDirection.NORTH, ForgeDirection.SOUTH)) {
			return 0;
		} else if (directionsMatch(a, b, ForgeDirection.EAST, ForgeDirection.WEST)) {
			return 1;
		} else if (directionsMatch(a, b, ForgeDirection.NORTH, ForgeDirection.EAST)) {
			return 2;
		} else if (directionsMatch(a, b, ForgeDirection.NORTH, ForgeDirection.WEST)) {
			return 3;
		} else if (directionsMatch(a, b, ForgeDirection.SOUTH, ForgeDirection.EAST)) {
			return 4;
		} else if (directionsMatch(a, b, ForgeDirection.SOUTH, ForgeDirection.WEST)) {
			return 5;
		} else {
			throw new RuntimeException("No metadata for ring platform directions (a: " + a + "b: " + b + ")");
		}
	}
	
	public Pair<ForgeDirection, ForgeDirection> getOrientationFromIndex(int i) {
		switch (i) {
		case 0: return new Pair(ForgeDirection.NORTH, ForgeDirection.SOUTH);
		case 1: return new Pair(ForgeDirection.EAST, ForgeDirection.WEST);
		case 2: return new Pair(ForgeDirection.NORTH, ForgeDirection.EAST);
		case 3: return new Pair(ForgeDirection.NORTH, ForgeDirection.WEST);
		case 4: return new Pair(ForgeDirection.SOUTH, ForgeDirection.EAST);
		case 5: return new Pair(ForgeDirection.SOUTH, ForgeDirection.WEST);
		default: throw new RuntimeException("Invalid direction index: " + i);
		}
	}
	
	@Override
	public void registerBlockIcons(IIconRegister register) {
		super.registerBlockIcons(register);
		for (int i = 0; i<6; i++) {
			Pair<ForgeDirection, ForgeDirection> orientation = this.getOrientationFromIndex(i);
			icons[i] = register.registerIcon(
					ClientProxy.MOD_ID + ":ringPlatform_"
					+ orientation.X.toString().substring(0, 1) + orientation.Y.toString().substring(0, 1));
		}
	}
	
	@Override
	public IIcon getIcon(int side, int metadata) {
		if (side == 0 || side == 1) {
			return this.icons[metadata];
		} else {
			return this.blockIcon;
		}
	}
	
	public void setOrientation(World world, int x, int y, int z) {
		int metadata;
		ForgeDirection firstBlock = null;
		for (int i = 2; i < 6; i++) {
			ForgeDirection d = ForgeDirection.getOrientation(i);
			if (world.getBlock(x + d.offsetX, y, z + d.offsetZ) == SDBlock.ringPlatform) {
				if (firstBlock == null) {
					firstBlock = d;
				} else {
					world.setBlockMetadataWithNotify(x, y, z, this.getIndexFromOrientation(firstBlock, d), 3);
					return;
				}
			}
		}
		if (firstBlock != null) {
			world.setBlockMetadataWithNotify(x, y, z, this.getIndexFromOrientation(firstBlock, firstBlock.getOpposite()), 3);
		}
	}
	
	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		for (int i = 2; i<6; i++) {
			ForgeDirection d = ForgeDirection.getOrientation(i);
			if (world.getBlock(x + d.offsetX, y, z + d.offsetZ) == SDBlock.ringPlatform) {
				this.setOrientation(world, x + d.offsetX, y, z + d.offsetZ);
			}
		}
		this.setOrientation(world, x, y, z);
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z,
			EntityPlayer player, int par1, float par2, float par3, float par4) {
		// Dont start a transport if one is already in progress
		if (world.getEntitiesWithinAABB(EntityTransporterRings.class, AxisAlignedBB.getBoundingBox(x - 1, y, z - 1, x + 1, y + 2, z + 1)).size() > 0) {
			return true;
		}
		Structure s = Structure.detectStructure(world, x, y, z, SpaceDistortion.transporterRingsShape, SpaceDistortion.templateBlockInfo, ForgeDirection.UP);
		if (s != null) {
			//world.spawnEntityInWorld(new EntityTransporterRings(world, s.x + 2, s.y + 1, s.z + 2));
			player.openGui(SpaceDistortion.instance, 1, world, x, y, z);
			return true;
		} else {
			return false;
		}
	}
}