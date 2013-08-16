package jw.spacedistortion.client.gui;

import jw.spacedistortion.common.CommonProxy;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;

public class GuiDHDButton extends GuiButton {
	public static int GlyphWidth = 32;
	public static int GlyphHeight = 32;
	
	public GuiDHDButton(int x, int y) {
		super(0, x, y, GlyphWidth, GlyphHeight, "");
	}
	
	@Override
	public void drawButton(Minecraft mc, int par1, int par2) {
		if (this.drawButton) {
			GL11.glColor4f(1, 1, 1, 1);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture(CommonProxy.GLYPHS_PNG));
			this.drawTexturedModalRectWithScale(this.xPosition, this.yPosition, 0, 0, GlyphWidth, GlyphHeight, 2);
		}
	}
	
	public void drawTexturedModalRectWithScale(int screenX, int screenY, int u, int v, int textureWidth, int textureHeight, int scale) {
		float var7 = 0.00390625F * scale;
		float var8 = 0.00390625F * scale;
		int width = textureWidth / scale;
		int height = textureHeight / scale;
		Tessellator var9 = Tessellator.instance;
		var9.startDrawingQuads();
		var9.addVertexWithUV((double) (screenX + 0), (double) (screenY + height), (double) this.zLevel, (double) ((float) (u + 0) * var7), (double) ((float) (v + height) * var8));
		var9.addVertexWithUV((double) (screenX + width), (double) (screenY + height), (double) this.zLevel, (double) ((float) (u + width) * var7), (double) ((float) (v + height) * var8));
		var9.addVertexWithUV((double) (screenX + width), (double) (screenY + 0), (double) this.zLevel, (double) ((float) (u + width) * var7), (double) ((float) (v + 0) * var8));
		var9.addVertexWithUV((double) (screenX + 0), (double) (screenY + 0), (double) this.zLevel, (double) ((float) (u + 0) * var7), (double) ((float) (v + 0) * var8));
		var9.draw();
	}
}
