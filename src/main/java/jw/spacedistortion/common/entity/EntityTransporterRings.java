package jw.spacedistortion.common.entity;

import java.util.List;

import jw.spacedistortion.Triplet;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class EntityTransporterRings extends Entity {
	public EntityTransporterRingsPart[] parts;
	// Stores all information about the animation sequence 0-40 = rising, 40-60 = transporting, 60-100 = lowering
	public int animationTimer = 0;
	public Triplet<Integer, Integer, Integer> dst;
	
	public EntityTransporterRings(World world) {
		super(world);
	}
	
	public EntityTransporterRings(World world, float x, float y, float z, Triplet<Integer, Integer, Integer> dst) {
		this(world);
		this.preventEntitySpawning = true;
		this.dst = dst;
		this.setPosition(x, y, z);
	}
	
	private void initParts() {
		this.parts = new EntityTransporterRingsPart[4];
		for (int i = 0; i < parts.length; i++) {
			ForgeDirection d = ForgeDirection.getOrientation(i + 2);
			parts[i] = new EntityTransporterRingsPart(this.worldObj, this, this.posX, this.posY, this.posZ, ForgeDirection.getOrientation(i + 2));
			//this.worldObj.spawnEntityInWorld(parts[i]);
		}
	}
	
	@Override
	public void setPosition(double x, double y, double z) {
		this.posX = x;
		this.posY = y;
		this.posZ = z;
		this.boundingBox.setBounds(x - 1, y, z - 1, x + 1, y + 2, z + 1);
		if (this.parts == null) {
			this.initParts();
		}
		for (int i = 0; i < this.parts.length; i++) {
			parts[i].setPosition(x, y, z);		
		}
	}
	
	@Override
	protected void entityInit() { }
	
	@Override
	public boolean isBurning() {
		return false;
	}
	
	@Override
	public float getShadowSize() {
		return 0;
	}
	
	@Override
	public void onUpdate() {
		super.onUpdate();
		for (int i = 0; i < parts.length; i++) {
			if (parts[i] != null) {
				parts[i].onUpdate();
			}
		}
		if (this.animationTimer == 50) {
			this.doTransport((int)this.posX, (int)this.posY,(int)this.posZ + 6);
		} else if (this.animationTimer == 100) {
			this.isDead = true;
		}
		this.animationTimer++;
	}
	
	public void doTransport(int x, int y, int z) {
		if (this.dst == null) {
			return;
		}
		// Move blocks
		for (int xo = -1; xo < 1; xo++) {
			for (int yo = 0; yo < 2; yo++) {
				for (int zo = -1; zo < 1; zo++) {
					Triplet<Integer, Integer, Integer> src = new Triplet<Integer, Integer, Integer>((int)this.posX + xo, (int)this.posY + yo, (int)this.posZ + zo);
					
					Block srcBlock = this.worldObj.getBlock(src.X, src.Y, src.Z);
					int srcMeta = this.worldObj.getBlockMetadata(src.X, src.Y, src.Z);
					Block dstBlock = this.worldObj.getBlock(this.dst.X, this.dst.Y, this.dst.Z);
					int dstMeta = this.worldObj.getBlockMetadata(this.dst.X, this.dst.Y, this.dst.Z);
					
					// switch the blocks and their metadata
					this.worldObj.setBlock(src.X, src.Y, src.Z, dstBlock, dstMeta, 3);
					this.worldObj.setBlock(this.dst.X, this.dst.Y, this.dst.Z, srcBlock, srcMeta, 3);
				}
			}
		}
		// Move entities
		List srcEntities = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox);
		List dstEntities = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, AxisAlignedBB.getBoundingBox(x - 1, y, z - 1, x + 1, y + 2, z + 1));
		for (Object e : srcEntities) {
			if (!(e instanceof EntityTransporterRings)) {
				Entity entity = (Entity)e;
				entity.setPosition(x - this.posX + entity.posX, y - this.posY + entity.posY, z - this.posZ + entity.posZ);
			}
		}
		for (Object e : dstEntities) {
			if (!(e instanceof EntityTransporterRings)) {
				Entity entity = (Entity)e;
				entity.setPosition(this.posX - x + entity.posX, this.posY - y + entity.posY, this.posZ - z + entity.posZ);
			}
		}
	}
	
	@Override
	public Entity[] getParts() {
		//return this.parts;
		return super.getParts();
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) { }

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) { }
}
