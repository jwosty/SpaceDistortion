package jw.spacedistortion.common.entity;

import java.util.List;

import jw.spacedistortion.Triplet;
import jw.spacedistortion.common.EntitySpawnPreventer;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
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
	public void setPositionAndRotation(double x, double y, double z, float pitch, float yaw) {
		this.setLocationAndAngles(x, y, z, pitch, yaw);
	}
	
	@Override
	public void setPositionAndRotation2(double x, double y, double z, float pitch, float yaw, int par) {
		this.setLocationAndAngles(x, y, z, pitch, yaw);
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
			if (this.dst != null) {
				this.doTransport(this.dst.X, this.dst.Y, this.dst.Z);
			}
		} else if (this.animationTimer >= 100) {
			this.isDead = true;
		}
		this.animationTimer++;
	}
	
	public void doTransport(int x, int y, int z) {
		doTransportBlocks(x, y, z);
		doTransportEntities(x, y, z);
	}

	private void doTransportEntities(int x, int y, int z) {
		// Move entities
		List srcEntities = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox);
		List dstEntities = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, AxisAlignedBB.getBoundingBox(x - 1, y, z - 1, x + 1, y + 2, z + 1));
		for (Object e : srcEntities) {
			if (!(e instanceof EntityTransporterRings)) {
				Entity entity = (Entity) e;
				this.teleportEntityOrPlayer(entity, x - this.posX + entity.posX, y - this.posY + entity.posY, z - this.posZ + entity.posZ);
			}
		}
		for (Object e : dstEntities) {
			if (!(e instanceof EntityTransporterRings)) {
				Entity entity = (Entity) e;
				this.teleportEntityOrPlayer(entity, this.posX - x + entity.posX, this.posY - y + entity.posY, this.posZ - z + entity.posZ);
			}
		}
	}
	
	private void doTransportBlocks(int x, int y, int z) {
		EntitySpawnPreventer spawnPreventer = new EntitySpawnPreventer(true);
		spawnPreventer.filter = EntityItem.class;
		// Move blocks
		for (int xo = -1; xo < 1; xo++) {
			for (int yo = 0; yo < 2; yo++) {
				for (int zo = -1; zo < 1; zo++) {
					Triplet<Integer, Integer, Integer> src = new Triplet<Integer, Integer, Integer>((int)this.posX + xo, (int)this.posY + yo, (int)this.posZ + zo);
					Triplet<Integer, Integer, Integer> dst = new Triplet<Integer, Integer, Integer>(x + xo, y + yo, z + zo);
					
					Block srcBlock = this.worldObj.getBlock(src.X, src.Y, src.Z);
					int srcMeta = this.worldObj.getBlockMetadata(src.X, src.Y, src.Z);
					Block dstBlock = this.worldObj.getBlock(dst.X, dst.Y, dst.Z);
					int dstMeta = this.worldObj.getBlockMetadata(dst.X, dst.Y, dst.Z);
					
					TileEntity srcTileEntity = this.worldObj.getTileEntity(src.X, src.Y, src.Z);
					NBTTagCompound srcTag = null;
					if (srcTileEntity != null) {
						srcTag = new NBTTagCompound();
						srcTileEntity.writeToNBT(srcTag);
						srcTag.setInteger("x", src.X);
						srcTag.setInteger("y", src.Y);
						srcTag.setInteger("z", src.Z);
					}
					
					TileEntity dstTileEntity = this.worldObj.getTileEntity(dst.X, dst.Y, dst.Z);
					NBTTagCompound dstTag = null;
					if (dstTileEntity != null) {
						dstTag = new NBTTagCompound();
						dstTileEntity.writeToNBT(dstTag);
						dstTag.setInteger("x", dst.X);
						dstTag.setInteger("y", dst.Y);
						dstTag.setInteger("z", dst.Z);
					}
					
					// switch blocks and metadata
					this.worldObj.setBlock(src.X, src.Y, src.Z, dstBlock, dstMeta, 3);
					this.worldObj.setBlock(dst.X, dst.Y, dst.Z, srcBlock, srcMeta, 3);
					// switch tile entities
					if (srcTileEntity != null) {
						this.worldObj.setTileEntity(dst.X, dst.Y, dst.Z, TileEntity.createAndLoadEntity(srcTag));
						this.worldObj.markBlockForUpdate(dst.X, dst.Y, dst.Z);
					}
					if (dstTileEntity != null) {
						this.worldObj.setTileEntity(src.X, src.Y, src.Z, TileEntity.createAndLoadEntity(dstTag));
						this.worldObj.markBlockForUpdate(src.X, src.Y, src.Z);
					}
				}
			}
		}
		spawnPreventer.unregister();
	}
	
	private void teleportEntityOrPlayer(Entity e, double x, double y, double z) {
		if (e instanceof EntityPlayerMP) {
			((EntityPlayerMP) e).playerNetServerHandler.setPlayerLocation(x, y, z, e.rotationYaw, e.rotationPitch);
		} else {
			e.setLocationAndAngles(x, y, z, e.rotationYaw, e.rotationPitch);
		}
	}
	
	@Override
	public Entity[] getParts() {
		return this.parts;
	}
	
	@Override
	protected void readEntityFromNBT(NBTTagCompound tag) { 
		this.animationTimer = tag.getInteger("timer");
		if (tag.hasKey("dstX") && tag.hasKey("dstY") && tag.hasKey("dstZ")) {
			this.dst = new Triplet<Integer, Integer, Integer>(tag.getInteger("dstX"), tag.getInteger("dstY"), tag.getInteger("dstZ"));
		}
	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound tag) {
		tag.setInteger("timer", this.animationTimer);
		if (this.dst != null) {
			tag.setInteger("dstX", this.dst.X);
			tag.setInteger("dstY", this.dst.Y);
			tag.setInteger("dstZ", this.dst.Z);
		}
	}
}
