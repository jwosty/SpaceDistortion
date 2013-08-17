package jw.spacedistortion.client.gui;

import jw.spacedistortion.common.CommonProxy;
import jw.spacedistortion.common.block.BlockStargateController;
import jw.spacedistortion.common.block.SDBlock;

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
	/**
	 * Keeps track of the current position of the coordinate that the player is
	 * inputting
	 */
	public int currentCoordinate = 0;
	/**
	 * Represents the coordinates the player has entered thus so far: the first
	 * 6 decimal places are the destination chunk's x coordinate, the next 6 are
	 * the destination chunk's z coordinate, and the last 2 places specify the
	 * dimension
	 */
	public byte[] address = new byte[7];
	private int dhdX;
	private int dhdY;
	private int dhdZ;
	
	public GuiDHD(int dhdX, int dhdY, int dhdZ) {
		this.dhdX = dhdX;
		this.dhdY = dhdY;
		this.dhdZ = dhdZ;
	}

	// The x and y position of the DHD panel top corner
	public int getPanelX() {
		return (this.width - GuiDHDButton.GlyphSheetWidth) / 2;
	}

	public int getPanelY() {
		return ((this.height - GuiDHDButton.GlyphSheetHeight) / 2)
				- GuiDHDButton.GlyphHeight;
	}

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

		// Create the address display at the top of the DHD
		for (int c = 0; c < 7; c++) {
			// Use a button for simplicity instead of a whole new Gui element
			// for this, setting the glyph to the empty slot so it doesn't draw
			// yet (b.drawButton doesn't seem to allow one to change it later)
			GuiDHDButton b = new GuiDHDButton(this.getPanelX() + (gw * c),
					this.getPanelY(), (byte) 39);
			b.isActivated = true;
			b.enabled = false;
			// b.drawButton = false;
			this.controlList.add(b);
		}

		// Create the buttons
		for (int glyphID = 0; glyphID < 39; glyphID++) {
			// Calculate the x and y position of the glyph (in the order of
			// appearance on the sprite sheet), offsetting the y by 1 glyph
			// in order to display the button panel lower
			int x = this.getPanelX() + (glyphID % (gsw / gw) * gw);
			int y = this.getPanelY() + (glyphID / (gsw / gw) * gh) + (gh * 2);
			// Finally, add the button
			this.controlList.add(new GuiDHDButton(x, y, (byte) glyphID));
		}
	}

	@Override
	public void actionPerformed(GuiButton guiButton) {
		if (guiButton.enabled && guiButton instanceof GuiDHDButton) {
			GuiDHDButton b = (GuiDHDButton) guiButton;
			// First, apply the FX: make the button glow orange
			b.isActivated = true;
			// Encode the selected coordinate into the address (note that
			// the glyph is pretty much a base 39 number)
			address[currentCoordinate] = b.glyphID;
			// Set the appropriate display button's glyph and show it
			GuiDHDButton display = (GuiDHDButton) this.controlList
					.get(currentCoordinate);
			display.glyphID = b.glyphID;
			currentCoordinate++;
			if (currentCoordinate == 7) {
				//System.out.println(address);
				((BlockStargateController)SDBlock.stargateController).addressReceived(address, this.dhdX, this.dhdY, this.dhdZ);
				this.mc.displayGuiScreen((GuiScreen) null);
			}
		}
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		this.drawDefaultBackground();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D,
				mc.renderEngine.getTexture(CommonProxy.DHD_PNG));
		this.drawTexturedModalRect(this.getPanelX(), this.getPanelY(), 0, 0,
				256, 256);
		super.drawScreen(par1, par2, par3);
	}
}