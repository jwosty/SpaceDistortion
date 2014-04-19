package jw.spacedistortion.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiDHDButton extends GuiButton {
	public static int GlyphWidth = 32;
	public static int GlyphHeight = 32;
	public static int GlyphSheetWidth = 256;
	public static int GlyphSheetHeight = 160;
	public static ResourceLocation glyphTexture;
	public byte glyphID;
	public boolean isActivated = false;
	
	public GuiDHDButton(int x, int y, ResourceLocation glyphTexture, byte glyphID) {
		super(0, x, y, GlyphWidth, GlyphHeight, "");
		this.glyphTexture = glyphTexture;
		this.glyphID = glyphID;
	}
	
	@Override
	public void drawButton(Minecraft mc, int par1, int par2) {
		if (this.visible) {
			float r;
			float g;
			float b;
			if (this.isActivated) {
				// all glyphs are orange, except for the special center button (which is red)
				r = 1;
				b = 0;
				g = (this.glyphID == 39) ? 0 : 0.5f;
			} else {
				r = g = b = 0.25f;
			}
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			mc.getTextureManager().bindTexture(this.glyphTexture);
			GlyphRenderer.drawGlyph(
					this, mc, this.glyphTexture, mc.fontRenderer,
					this.xPosition, this.yPosition, this.glyphID, r, g, b, 1);
		}
	}
	
	@Override
	public void func_146113_a(SoundHandler soundHandler) {
		// Overrided to prevent the default button sound from playing
	}
}
