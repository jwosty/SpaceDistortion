package jw.spacedistortion.client.gui;

import jw.spacedistortion.common.CommonProxy;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;

public class GuiDHDButton extends GuiButton {
	public static int GlyphWidth = 32;
	public static int GlyphHeight = 32;
	public static int GlyphSheetWidth = 256;
	public static int GlyphSheetHeight = 160;
	public byte glyphID;
	
	public GuiDHDButton(int x, int y, byte glyphID) {
		super(0, x, y, GlyphWidth, GlyphHeight, "");
		this.glyphID = glyphID;
	}
	
	@Override
	public void drawButton(Minecraft mc, int par1, int par2) {
		if (this.drawButton) {
			GL11.glColor4f(0, 0, 0, 1);
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture(CommonProxy.GLYPHS_PNG));
			this.drawTexturedModalRect(this.xPosition, this.yPosition,
					this.glyphID % (GlyphSheetWidth / GlyphWidth) * GlyphWidth, this.glyphID / (GlyphSheetWidth / GlyphWidth) * GlyphHeight,
					GlyphWidth, GlyphHeight);
		}
	}
}
