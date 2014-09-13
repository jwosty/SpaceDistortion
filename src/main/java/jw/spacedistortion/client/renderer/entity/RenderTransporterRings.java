package jw.spacedistortion.client.renderer.entity;

import jw.spacedistortion.common.entity.EntityTransporterRings;
import jw.spacedistortion.common.entity.EntityTransporterRingsPart;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
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
				this.doRender(rings.parts[i], rings.animationTimer);
			}
		}
		
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		GL11.glPopMatrix();
		
	}

	public void doRender(EntityTransporterRingsPart part, double animationTimer) {
		GL11.glPushMatrix();
		GL11.glTranslated(-part.posX, -part.posY, -part.posZ);
		AxisAlignedBB bb = part.boundingBox;
		double numRings = 5;
		for (double i = 0; i < 1; i += (1D/numRings)) {
			double minY = bb.minY + (animationTimer * part.height) - (i * part.height);
			if (minY + 0.001 >= part.posY) {
				this.renderAABB(bb.minX, minY, bb.minZ, bb.maxX, minY + (1 / numRings), bb.maxZ);
			}
		}
		GL11.glPopMatrix();
	}
	
	public static void renderAABB(double minX, double minY, double minZ,
			double maxX, double maxY, double maxZ) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        Tessellator tessellator = Tessellator.instance;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, -1.0F);
        tessellator.addVertex(minX, maxY, minZ);
        tessellator.addVertex(maxX, maxY, minZ);
        tessellator.addVertex(maxX, minY, minZ);
        tessellator.addVertex(minX, minY, minZ);
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        tessellator.addVertex(minX, minY, maxZ);
        tessellator.addVertex(maxX, minY, maxZ);
        tessellator.addVertex(maxX, maxY, maxZ);
        tessellator.addVertex(minX, maxY, maxZ);
        tessellator.setNormal(0.0F, -1.0F, 0.0F);
        tessellator.addVertex(minX, minY, minZ);
        tessellator.addVertex(maxX, minY, minZ);
        tessellator.addVertex(maxX, minY, maxZ);
        tessellator.addVertex(minX, minY, maxZ);
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        tessellator.addVertex(minX, maxY, maxZ);
        tessellator.addVertex(maxX, maxY, maxZ);
        tessellator.addVertex(maxX, maxY, minZ);
        tessellator.addVertex(minX, maxY, minZ);
        tessellator.setNormal(-1.0F, 0.0F, 0.0F);
        tessellator.addVertex(minX, minY, maxZ);
        tessellator.addVertex(minX, maxY, maxZ);
        tessellator.addVertex(minX, maxY, minZ);
        tessellator.addVertex(minX, minY, minZ);
        tessellator.setNormal(1.0F, 0.0F, 0.0F);
        tessellator.addVertex(maxX, minY, minZ);
        tessellator.addVertex(maxX, maxY, minZ);
        tessellator.addVertex(maxX, maxY, maxZ);
        tessellator.addVertex(maxX, minY, maxZ);
        tessellator.setTranslation(0.0D, 0.0D, 0.0D);
        tessellator.draw();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
    }
	
	@Override
	public boolean isStaticEntity() {
		return super.isStaticEntity();
	}
	
	@Override
	protected ResourceLocation getEntityTexture(Entity entity) {
		return null;
	}

}
