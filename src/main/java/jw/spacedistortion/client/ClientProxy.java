package jw.spacedistortion.client;

import jw.spacedistortion.client.renderer.entity.RenderTransporterRings;
import jw.spacedistortion.common.CommonProxy;
import jw.spacedistortion.common.entity.EntityTransporterRings;
import cpw.mods.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
	@Override
	public void registerRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityTransporterRings.class, new RenderTransporterRings());
	}
}
