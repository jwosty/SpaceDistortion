package jw.spacedistortion.client.renderer.entity;

import jw.spacedistortion.common.CommonProxy;
import jw.spacedistortion.common.entity.EntityTransporterRings;
import jw.spacedistortion.common.entity.EntityTransporterRingsPart;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class RenderTransporterRings extends RenderEntity {
	public static ResourceLocation[] ringTextures = new ResourceLocation[] {
		new ResourceLocation(CommonProxy.MOD_ID + ":textures/entities/ring-N.png"),
		new ResourceLocation(CommonProxy.MOD_ID + ":textures/entities/ring-S.png"),
		new ResourceLocation(CommonProxy.MOD_ID + ":textures/entities/ring-E.png"), 
		new ResourceLocation(CommonProxy.MOD_ID + ":textures/entities/ring-W.png") };
	
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

	public void doRender(EntityTransporterRingsPart part, int animationTimer) {
		GL11.glPushMatrix();
		GL11.glTranslated(-part.posX, -part.posY, -part.posZ);
		// A big complicated scary line to get a height factor (so 0..1) from the animation timer rules
		double interp = animationTimer < 40 ? ((double)animationTimer / 40D) : (animationTimer < 60 ? 1 : ((double)(100 - animationTimer) / 40));
		AxisAlignedBB bb = part.boundingBox;
		double numRings = 5;
		for (double i = 0; i < 1; i += (1D/numRings)) {
			double minY = bb.minY + (interp * part.height) - (i * part.height);
			if (minY + 0.2 >= part.posY) {
				this.renderAABB(bb.minX, minY, bb.minZ, bb.maxX, minY + (1 / numRings), bb.maxZ, ringTextures[part.segment().ordinal() - 2]);
			}
		}
		GL11.glPopMatrix();
	}
	
	public void renderAABB(double minX, double minY, double minZ,
			double maxX, double maxY, double maxZ, ResourceLocation texture) {
        //GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
		Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
		Tessellator t = Tessellator.instance;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		t.startDrawingQuads();
		t.setNormal(0.0F, 0.0F, -1.0F);
		t.addVertex(minX, maxY, minZ);
		t.addVertex(maxX, maxY, minZ);
		t.addVertex(maxX, minY, minZ);
		t.addVertex(minX, minY, minZ);
		t.setNormal(0.0F, 0.0F, 1.0F);
		t.addVertex(minX, minY, maxZ);
		t.addVertex(maxX, minY, maxZ);
		t.addVertex(maxX, maxY, maxZ);
		t.addVertex(minX, maxY, maxZ);
		
		t.setNormal(0.0F, -1.0F, 0.0F);
		t.addVertexWithUV(minX, minY, minZ, 0, 0);
		t.addVertexWithUV(maxX, minY, minZ, 1, 0);
		t.addVertexWithUV(maxX, minY, maxZ, 1, 1);
		t.addVertexWithUV(minX, minY, maxZ, 0, 1);
		t.setNormal(0.0F, 1.0F, 0.0F);
		t.addVertexWithUV(minX, maxY, maxZ, 0, 1);
		t.addVertexWithUV(maxX, maxY, maxZ, 1, 1);
		t.addVertexWithUV(maxX, maxY, minZ, 1, 0);
		t.addVertexWithUV(minX, maxY, minZ, 0, 0);
		
		t.setNormal(-1.0F, 0.0F, 0.0F);
		t.addVertex(minX, minY, maxZ);
		t.addVertex(minX, maxY, maxZ);
		t.addVertex(minX, maxY, minZ);
		t.addVertex(minX, minY, minZ);
		t.setNormal(1.0F, 0.0F, 0.0F);
		t.addVertex(maxX, minY, minZ);
		t.addVertex(maxX, maxY, minZ);
		t.addVertex(maxX, maxY, maxZ);
		t.addVertex(maxX, minY, maxZ);
		t.setTranslation(0.0D, 0.0D, 0.0D);
		t.draw();
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
