package jw.spacedistortion.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class EntityTransporterRingsPart extends Entity {
	EntityTransporterRings parent;
	ForgeDirection segment;
	
	public EntityTransporterRingsPart(World world, EntityTransporterRings parent, double x, double y, double z, ForgeDirection segment) {
		super(world);
		this.parent = parent;
		this.segment = segment;
		this.setPosition(x, y, z);
	}
	
	@Override
	protected void entityInit() {}
	
	@Override
	public void onUpdate() {
		this.isDead = this.parent.isDead;
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound var1) {}

	@Override
	protected void writeEntityToNBT(NBTTagCompound var1) {}

}
