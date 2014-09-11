package jw.spacedistortion.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class EntityTransporterRings extends Entity {
	public EntityTransporterRingsPart[] parts = new EntityTransporterRingsPart[4];
	
	public EntityTransporterRings(World world) {
		super(world);
		//this.setSize(0, 0);
		//this.setSize(1.0f, 1.0f);
	}
	
	public EntityTransporterRings(World world, float x, float y, float z) {
		this(world);
		this.preventEntitySpawning = true;
		this.setPosition(x, y, z);
		
		for (int i = 0; i < parts.length; i++) {
			parts[i] = new EntityTransporterRingsPart(world, this, x, y, z, ForgeDirection.getOrientation(i + 1));
			//world.spawnEntityInWorld(parts[i]);
		}
		
	}

	@Override
	protected void entityInit() {}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		this.isDead = ((EntityPlayer) this.worldObj.playerEntities.get(0)).isSneaking();
		for (int i = 0; i < parts.length; i++) {
			if (parts[i] != null) {
				parts[i].onUpdate();
			}
		}
		//this.isDead = true;
		/*
		this.startY++;
		if (startY >= 50) {
			this.isDead = true;
		}
		*/
	}
	
	@Override
	public Entity[] getParts() {
		//return this.parts;
		return super.getParts();
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) {
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {	
	}
}
