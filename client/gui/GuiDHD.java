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
		
		// "Shortcuts" for frequently accessed constants
		int gsw = GuiDHDButton.GlyphSheetWidth;
		int gsh = GuiDHDButton.GlyphSheetHeight;
		int gw = GuiDHDButton.GlyphWidth;
		int gh = GuiDHDButton.GlyphHeight;
		
		// Calculate the top corner of the DHD panel
		int panelX = (this.width - gsw) / 2;
		int panelY = (this.height - gsh) / 2;
		
		// Create the address display at the top of the DHD
		for (int c = 0; c < 7; c++) {
			
		}
		
		// Create the buttons
		for(int glyphID = 0; glyphID < 39; glyphID++) {
			// Calculate the x and y position of the glyph (in the order of
			// appearance on the sprite sheet), offsetting the y by 1 glyph
			// in order to display the button panel lower
			int x = panelX + (glyphID % (gsw / gw) * gw);
			int y = panelY + (glyphID / (gsw / gw) * gh) + gh;
			// Finally, add the button
			this.controlList.add(new GuiDHDButton(x, y, (byte) glyphID));
		}
	}
	
	@Override
	public void actionPerformed(GuiButton guiButton) {
		if (guiButton.enabled && guiButton instanceof GuiDHDButton) {
			GuiDHDButton b = (GuiDHDButton)guiButton;
			b.isActivated = true;
		}
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		this.drawDefaultBackground();
		super.drawScreen(par1, par2, par3);
	}
}