package jw.spacedistortion.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

public class GuiRingPlatformButton extends GuiButton {
	public boolean isWhite = false;
	
	public GuiRingPlatformButton(int x, int y, boolean isWhite) {
		super(0, x, y, 4, 4, "");
		this.isWhite = isWhite;
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
		if (this.isWhite) {
			t.setColorOpaque(255, 255, 255);
		} else {
			t.setColorOpaque(255, 255, 0);
		}
		t.addVertex(0, 0, 0);
		t.addVertex(0, 4, 0);
		t.addVertex(4, 4, 0);
		t.addVertex(4, 0, 0);
		t.draw();
		GL11.glPopMatrix();
	}
}
