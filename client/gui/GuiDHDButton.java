package jw.spacedistortion.client.gui;

import jw.spacedistortion.common.CommonProxy;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.Tessellator;

public class GuiDHDButton extends GuiButton {
	public static int GlyphWidth = 48;
	public static int GlyphHeight = 48;
	
	public GuiDHDButton(int x, int y) {
		super(0, x, y, GlyphWidth, GlyphHeight, "");
	}
	
	@Override
	public void drawButton(Minecraft mc, int par1, int par2) {
		if (this.drawButton) {
			
		}
	}
}
