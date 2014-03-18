package jw.spacedistortion.client.gui;

import jw.spacedistortion.common.CommonProxy;
import jw.spacedistortion.common.block.SDBlock;
import jw.spacedistortion.common.network.ChannelHandler;
import jw.spacedistortion.common.network.packet.PacketDHDEnterGlyph;
import jw.spacedistortion.common.tileentity.StargateControllerState;
import jw.spacedistortion.common.tileentity.TileEntityStargateController;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.opengl.GL11;

/*
 * Class written using GuiEditSign as a template
 */
public class GuiDHD extends GuiScreen {
	public static ResourceLocation glyphTexture = new ResourceLocation(CommonProxy.MOD_ID + ":" + "textures/gui/glyphs.png");
	public static ResourceLocation backgroundTexture = new ResourceLocation(CommonProxy.MOD_ID + ":" + "textures/gui/DHD.png");
	
	public TileEntityStargateController tileEntity;
	
	public GuiDHD(TileEntityStargateController controllerTileEntity) {
		this.tileEntity = controllerTileEntity;
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}

	// The x screen position of the DHD panel top corner
	public int getPanelX() {
		return (this.width - GuiDHDButton.GlyphSheetWidth) / 2;
	}
	
	// The y screen position of the DHD panel top corner
	public int getPanelY() {
		return ((this.height - GuiDHDButton.GlyphSheetHeight) / 2) - GuiDHDButton.GlyphHeight;
	}

	@Override
	public void initGui() {
		this.buttonList.clear();

		// "Shortcuts" for frequently accessed constants
		int gsw = GuiDHDButton.GlyphSheetWidth;
		int gsh = GuiDHDButton.GlyphSheetHeight;
		int gw = GuiDHDButton.GlyphWidth;
		int gh = GuiDHDButton.GlyphHeight;

		// Create the address display at the top of the DHD
		for (int c = 0; c < 7; c++) {
			// Use a button for simplicity instead of a whole new Gui element for this
			GuiDHDButton b = new GuiDHDButton(this.getPanelX() + (gw * c), this.getPanelY(), this.glyphTexture, this.tileEntity.dialingAddress[c]);
			b.visible = true;
			b.isActivated = true;
			b.enabled = true;
			this.buttonList.add(b);
		}

		// Create the buttons
		for (byte glyphID = 0; glyphID < 40; glyphID++) {
			// Calculate the x and y position of the glyph (in the order of
			// appearance on the sprite sheet), offsetting the y by 1 glyph
			// in order to display the button panel lower.
			int g;
			if (glyphID < 19) {
				g = glyphID;
			} else if (glyphID == 39) {
				g = 19;
			} else {
				g = glyphID + 1;
			}
			// Calculate the x and y position
			int x = this.getPanelX() + (g % (gsw / gw) * gw);
			int y = this.getPanelY() + (g / (gsw / gw) * gh) + (gh * 2);
			// Finally, add the button
			GuiDHDButton button = new GuiDHDButton(x, y, this.glyphTexture, (byte) glyphID);
			if (ArrayUtils.contains(this.tileEntity.dialingAddress, glyphID)) {
				button.isActivated = true;
			}
			this.buttonList.add(button);
		}
	}
	
	@Override
	public void actionPerformed(GuiButton guiButton) {
		GuiDHDButton b = (GuiDHDButton) guiButton;
		if (this.tileEntity.currentGlyphIndex < 7) {
			// Update the GUI elements before sending to the server
			((GuiDHDButton) this.buttonList.get(this.tileEntity.currentGlyphIndex)).glyphID = b.glyphID;
			b.isActivated = true; }
			
		// Kindly ask the server to input another glyph into the stargate controller tile entity
		ChannelHandler.clientSendPacket(new PacketDHDEnterGlyph(
				this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord, b.glyphID));
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		this.drawDefaultBackground();
		GL11.glDisable(GL11.GL_LIGHTING);
		mc.getTextureManager().bindTexture(this.backgroundTexture);
		this.drawTexturedModalRect(this.getPanelX(), this.getPanelY(), 0, 0,
				256, 256);
		super.drawScreen(par1, par2, par3);
	}
}