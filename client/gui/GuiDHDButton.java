package jw.spacedistortion.client.gui;

import jw.spacedistortion.common.CommonProxy;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiDHDButton extends GuiButton {
	public static int GlyphWidth = 48;
	public static int GlyphHeight = 48;
	
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
			this.drawTexturedModalRect(this.xPosition, this.yPosition, 20, 20, GlyphWidth * 5, GlyphHeight * 5);
		}
	}
}
