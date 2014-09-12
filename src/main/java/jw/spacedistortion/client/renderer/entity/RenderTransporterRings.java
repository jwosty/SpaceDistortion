package jw.spacedistortion.client.renderer.entity;

import jw.spacedistortion.common.entity.EntityTransporterRings;
import jw.spacedistortion.common.entity.EntityTransporterRingsPart;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class RenderTransporterRings extends RenderEntity {
	@Override
	public void doRender(Entity ringsEntity, double interpX, double interpY, double interpZ,
			float par4, float par5) {
		
		EntityTransporterRings rings = (EntityTransporterRings) ringsEntity;
		GL11.glPushMatrix();
		GL11.glTranslated(interpX, interpY, interpZ);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		GL11.glPointSize(10);
		for (int i = 0; i < rings.parts.length; i++) {
			if (rings.parts[i] != null) {
				this.doRender(rings.parts[i]);
			}
		}
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();
		
	}

	public void doRender(EntityTransporterRingsPart part) {
		GL11.glPushMatrix();
		GL11.glTranslated(-part.posX, -part.posY, -part.posZ);
		super.renderOffsetAABB(part.boundingBox, 0, 0, 0);
		/*
		Tessellator t = Tessellator.instance;
		t.startDrawingQuads();
		t.setNormal(0.0F, 0.0F, -1.0F);
        t.addVertex(0, 1, 0);
        t.addVertex(1, 1, 0);
        t.addVertex(1, 0, 0);
        t.addVertex(0, 0, 0);
        t.setNormal(0.0F, 0.0F, 1.0F);
        t.addVertex(0, 0, 1);
        t.addVertex(1, 0, 1);
        t.addVertex(1, 1, 1);
        t.addVertex(0, 1, 1);
        t.setNormal(0.0F, -1.0F, 0.0F);
        t.addVertex(0, 0, 0);
        t.addVertex(1, 0, 0);
        t.addVertex(1, 0, 1);
        t.addVertex(0, 0, 1);
        t.setNormal(0.0F, 1.0F, 0.0F);
        t.addVertex(0, 1, 1);
        t.addVertex(1, 1, 1);
        t.addVertex(1, 1, 0);
        t.addVertex(0, 1, 0);
        t.setNormal(-1.0F, 0.0F, 0.0F);
        t.addVertex(0, 0, 1);
        t.addVertex(0, 1, 1);
        t.addVertex(0, 1, 0);
        t.addVertex(0, 0, 0);
        t.setNormal(1.0F, 0.0F, 0.0F);
        t.addVertex(1, 0, 0);
        t.addVertex(1, 1, 0);
        t.addVertex(1, 1, 1);
        t.addVertex(1, 0, 1);
		t.draw();
		*/
		GL11.glPopMatrix();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return null;
	}

}
