package jw.spacedistortion.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

public class GuiRingPlatformButton extends GuiButton {
	public int ringX;
	public int ringY;
	public int ringZ;
	public boolean isThis = false;
	
	public GuiRingPlatformButton(int screenX, int screenY, int ringX, int ringY, int ringZ, boolean isThis) {
		super(0, screenX, screenY, 2, 2, "");
		this.ringX = ringX;
		this.ringY = ringY;
		this.ringZ = ringZ;
		this.isThis = isThis;
	}
	
	@Override
	public void drawButton(Minecraft mc, int mx, int my) {
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		Tessellator t = Tessellator.instance;
		GL11.glTranslatef(this.xPosition, this.yPosition, 0);
		t.startDrawingQuads();
		if (this.isThis) {
			t.setColorOpaque(255, 255, 255);
		} else {
			t.setColorOpaque(255, 255, 0);
		}
		t.addVertex(0, 0, 0);
		t.addVertex(0, 2, 0);
		t.addVertex(2, 2, 0);
		t.addVertex(2, 0, 0);
		t.draw();
		GL11.glPopMatrix();
	}
}
