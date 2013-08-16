package jw.spacedistortion.client.gui;

import jw.spacedistortion.common.CommonProxy;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.event.EventPriority;
import net.minecraftforge.event.ForgeSubscribe;

/*
 * Class written using GuiEditSign as a template
 */
public class GuiDHD extends GuiScreen {
	@Override
	public boolean doesGuiPauseGame() {
		return true;
	}
	
	@Override
	public void initGui() {
		this.controlList.clear();
		for(int glyphID = 0; glyphID < 39; glyphID++) {
			int gsw = GuiDHDButton.GlyphSheetWidth;
			int gsh = GuiDHDButton.GlyphSheetHeight;
			int panelX = (this.width - gsw) / 2;
			int panelY = (this.height - gsh) / 2;
			int gw = GuiDHDButton.GlyphWidth;
			int gh = GuiDHDButton.GlyphHeight;
			int x = panelX + (glyphID % (gsw / gw) * gw);
			int y = panelY + (glyphID / (gsw / gw) * gh);
			this.controlList.add(new GuiDHDButton(x, y, (byte)glyphID));
		}
	}
	
	@Override
	public void actionPerformed(GuiButton guiButton) {
		if (guiButton.enabled && guiButton.id == 0 && guiButton instanceof GuiDHDButton) {
			GuiDHDButton b = (GuiDHDButton)guiButton;
			b.isActivated = !b.isActivated;
		}
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		this.drawDefaultBackground();
		super.drawScreen(par1, par2, par3);
	}
}