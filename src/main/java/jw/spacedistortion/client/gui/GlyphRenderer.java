package jw.spacedistortion.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

public abstract class GlyphRenderer {
	public static final BiMap<Byte, Character> glyphChars;
	
	static {
		ImmutableBiMap.Builder<Byte, Character> builder = new ImmutableBiMap.Builder<Byte, Character>();
		
		for (byte i = 0; i < 26; i++) {
			// 'a' = ASCII 97
			builder.put(i, (char)(i + 97));
		}
		
		for (byte i = 0; i < 10; i++) {
			// '0' = ASCII 48
			builder.put((byte)(i + 26), (char)(i + 48));
		}
		
		builder.put((byte)36, '-');
		builder.put((byte)37, '+');
		builder.put((byte)38, '.');
		
		glyphChars = builder.build();
	}
	
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
		if (glyphChars.containsKey(glyphID)) {
			fontRenderer.drawString(Character.toString(glyphChars.get(glyphID)), x + 2, y + 2, 0xFFFFFF);
		}
	}
}
