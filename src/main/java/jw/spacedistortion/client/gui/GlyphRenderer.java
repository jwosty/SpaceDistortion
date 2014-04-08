package jw.spacedistortion.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public abstract class GlyphRenderer {
	public static void drawGlyph(Gui gui, Minecraft mc, ResourceLocation glyphTexture, FontRenderer fontRenderer,
			int x, int y, byte glyphID, float red, float green, float blue, float alpha) {
		mc.getTextureManager().bindTexture(glyphTexture);
		GL11.glColor4f(red, green, blue, alpha);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		gui.drawTexturedModalRect(
				x, y, 
				glyphID * GuiDHDButton.GlyphWidth,
				glyphID / (GuiDHDButton.GlyphSheetWidth / GuiDHDButton.GlyphWidth) * GuiDHDButton.GlyphWidth,
				GuiDHDButton.GlyphWidth, GuiDHDButton.GlyphHeight);
	}
}
