package jw.spacedistortion.common;

import net.minecraft.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class EntitySpawnPreventer {
	public Class<? extends Entity> filter = Entity.class;
	public boolean isRegistered = false;
	
	public EntitySpawnPreventer(boolean register) {
		if (register) this.register();
	}
	
	public void register() {
		if (!this.isRegistered) {
			MinecraftForge.EVENT_BUS.register(this);
			this.isRegistered = true;
		}
	}
	
	public void unregister() {
		if (this.isRegistered) {
			MinecraftForge.EVENT_BUS.unregister(this);
			this.isRegistered = false;
		}
	}
	
	@SubscribeEvent
	public void entitySpawn(EntityJoinWorldEvent event) {
		event.setCanceled(true);
	}
	
	@Override
	public void finalize() {
		this.unregister();
	}
}
