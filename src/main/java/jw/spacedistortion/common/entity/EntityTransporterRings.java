package jw.spacedistortion.common.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class EntityTransporterRings extends Entity {
	public EntityTransporterRingsPart[] parts;
	
	public EntityTransporterRings(World world) {
		super(world);
		//this.setSize(0, 0);
		//this.setSize(1.0f, 1.0f);
	}
	
	public EntityTransporterRings(World world, float x, float y, float z) {
		this(world);
		this.preventEntitySpawning = true;
		this.setPosition(x, y, z);
	}

	@Override
	protected void entityInit() {
		this.parts = new EntityTransporterRingsPart[4];
		for (int i = 0; i < parts.length; i++) {
			ForgeDirection d = ForgeDirection.getOrientation(i + 2);
			parts[i] = new EntityTransporterRingsPart(this.worldObj, this, this.posX, this.posY, this.posZ, ForgeDirection.getOrientation(i + 2));
			//world.spawnEntityInWorld(parts[i]);
		}
	}
	
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
