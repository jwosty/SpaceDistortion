package jw.spacedistortion.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class EntityTransporterRingsPart extends Entity {
	// [[minX, maxX, minZ, maxZ], ... ]
	public static final double[][] Edges = {
		{ -1, 2, -2, -1 }, { -2, 1, 1, 2 },
		{ 1, 2, -1, 2 }, { -2, -1, -2, 1 } };
	
	EntityTransporterRings parent;
	ForgeDirection segment;
	
	public EntityTransporterRingsPart(World world, EntityTransporterRings parent, double x, double y, double z, ForgeDirection segment) {
		super(world);
		this.parent = parent;
		this.setPosition(segment, x, y, z);
	}
	
	
	@Override
	public void setPosition(double x, double y, double z) {
		this.setPosition(this.segment, x, y, z);
	}
	
	public void setPosition(ForgeDirection segment, double x, double y, double z) {
		if (segment == null | segment == ForgeDirection.UNKNOWN | segment == ForgeDirection.UP | segment == ForgeDirection.DOWN) {
			super.setPosition(x, y, z);
		} else {
			this.segment = segment;
			this.posX = x;
			this.posY = y;
			this.posZ = z;
			double[] e = Edges[segment.ordinal() - 2];
			this.boundingBox.minX = x + e[0];
			this.boundingBox.maxX = x + e[1];
			this.boundingBox.minZ = z + e[2];
			this.boundingBox.maxZ = z + e[3];
			this.boundingBox.minY = y;
			this.boundingBox.maxY = y;
			this.height = 2;
		}
	}
	
	@Override
	protected void entityInit() {}
	
	@Override
	public void onUpdate() {
		if (this.posX != this.parent.posX | this.posY != this.parent.posY | this.posZ != this.parent.posZ){
			this.setPosition(this.parent.posX, this.parent.posY, this.parent.posZ);
		}
		if (Math.abs(this.boundingBox.maxY - this.boundingBox.minY) < this.height) {
			this.boundingBox.maxY += ((1D / 12D));
			if ((Math.abs(this.boundingBox.maxY - this.boundingBox.minY) > this.height)) {
				this.boundingBox.maxY = this.boundingBox.minY + 2;
			}
		}
		this.isDead = this.parent.isDead;
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound var1) {}

	@Override
	protected void writeEntityToNBT(NBTTagCompound var1) {}

}
