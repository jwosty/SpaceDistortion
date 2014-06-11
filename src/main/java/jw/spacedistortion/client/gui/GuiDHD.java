package jw.spacedistortion.client.gui;

import jw.spacedistortion.common.CommonProxy;
import jw.spacedistortion.common.block.SDBlock;
import jw.spacedistortion.common.network.ChannelHandler;
import jw.spacedistortion.common.network.packet.PacketDHDEnterGlyph;
import jw.spacedistortion.common.tileentity.StargateControllerState.StargateControllerValid.StargateControllerActive;
import jw.spacedistortion.common.tileentity.StargateControllerState.StargateControllerValid.StargateControllerReady;
import jw.spacedistortion.common.tileentity.TileEntityStargateController;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.opengl.GL11;

public class GuiDHD extends GuiScreen {
	public static ResourceLocation glyphTexture = new ResourceLocation(CommonProxy.MOD_ID + ":" + "textures/gui/glyphs.png");
	public static ResourceLocation backgroundTexture = new ResourceLocation(CommonProxy.MOD_ID + ":" + "textures/gui/DHD.png");
	
	public TileEntityStargateController tileEntity;
	/** Memoizes the address of the stargate controller */
	public byte[] addressMemoization;
	public byte[] connectedMemoization;
	
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
		
		this.memoize();

		// "Shortcuts" for frequently accessed constants
		int gsw = GuiDHDButton.GlyphSheetWidth;
		int gsh = GuiDHDButton.GlyphSheetHeight;
		int gw = GuiDHDButton.GlyphWidth;
		int gh = GuiDHDButton.GlyphHeight;

		// Create the buttons
		for (byte i = 0; i < 40; i++) {
			// Calculate the x and y position of the glyph (in the order of
			// appearance on the sprite sheet), offsetting the y by 1 glyph
			// in order to display the button panel lower.
			int glyphID;
			if (i < 19) {
				glyphID = i;
			} else if (i == 19) {
				glyphID = 39;
			} else {
				glyphID = i - 1;
			}
			// Calculate the x and y position
			int x = this.getPanelX() + (i % (gsw / gw) * gw);
			int y = this.getPanelY() + (i / (gsw / gw) * gh) + (gh * 2);
			// Finally, add the button
			GuiDHDButton button = new GuiDHDButton(x, y, this.glyphTexture, (byte)glyphID);
			if (this.tileEntity.state instanceof StargateControllerReady
					&& ArrayUtils.contains(((StargateControllerReady)this.tileEntity.state).addressBuffer, (byte)glyphID)) {
				button.isActivated = true;
			}
			this.buttonList.add(button);
		}
	}
	
	public void memoize() {
		this.addressMemoization = SDBlock.stargateController.encodeAddress(
				this.tileEntity.xCoord >> 4, this.tileEntity.zCoord >> 4, this.tileEntity.getWorldObj().provider.dimensionId);
		if (this.tileEntity.state instanceof StargateControllerReady) {
			StargateControllerReady state = (StargateControllerReady) tileEntity.state;
			this.connectedMemoization = state.addressBuffer;
		} else if (this.tileEntity.state instanceof StargateControllerActive) {
			StargateControllerActive state = (StargateControllerActive) tileEntity.state;
			this.connectedMemoization = SDBlock.stargateController.encodeAddress(
					state.connectedXCoord >> 4, state.connectedZCoord >> 4, state.connectedDimension);
		}
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		GuiDHDButton b = (GuiDHDButton) button;
		b.isActivated = true;
		ChannelHandler.clientSendPacket(new PacketDHDEnterGlyph(
				this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord, b.glyphID));
		this.memoize();
	}
	
	@Override
	public void keyTyped(char keyChar, int keyID) {
		super.keyTyped(keyChar, keyID);
		if (GlyphRenderer.glyphChars.containsValue(keyChar)) {
			byte glyphID = GlyphRenderer.glyphChars.inverse().get(keyChar);
			for (Object e : this.buttonList) {
				GuiDHDButton b = (GuiDHDButton)e;
				if (((byte)b.glyphID) == glyphID) {
					b.isActivated = true;
				}
			}
			ChannelHandler.clientSendPacket(new PacketDHDEnterGlyph(
					this.tileEntity.xCoord, this.tileEntity.yCoord, this.tileEntity.zCoord, glyphID));
		}
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		this.drawDefaultBackground();
		// Draw background
		GL11.glDisable(GL11.GL_LIGHTING);
		mc.getTextureManager().bindTexture(this.backgroundTexture);
		this.drawTexturedModalRect(this.getPanelX(), this.getPanelY(), 0, 0,
				256, 256);
		// Draw the address the user is dialing
		/*
		int max;
		byte[] addressBarGlyphs;
		switch (this.tileEntity.state) {
		case ACTIVE_OUTGOING:
		case ACTIVE_INCOMING:
			max = this.addressMemoization.length;
			addressBarGlyphs = this.connectedMemoization;
			break;
		default:
			max = this.tileEntity.currentGlyphIndex;
			addressBarGlyphs = this.tileEntity.addressBuffer;
			break;
		}
		*/
		for (int i = 0; i < this.connectedMemoization.length; i++) {
			GlyphRenderer.drawGlyph(
					this, this.mc, this.glyphTexture, this.fontRendererObj,
					this.getPanelX() + (GuiDHDButton.GlyphWidth * i), this.getPanelY(),
					this.connectedMemoization[i],
					this.tileEntity.state.getGuiDisplayRed(),
					this.tileEntity.state.getGuiDisplayGreen(),
					this.tileEntity.state.getGuiDisplayBlue(),
					1);
		}
		// Draw the address of this GUI's tile entity
		for (int i = 0; i < this.addressMemoization.length; i++) {
			GlyphRenderer.drawGlyph(
					this, this.mc, this.glyphTexture, this.fontRendererObj,
					this.getPanelX() + (GuiDHDButton.GlyphWidth * i), this.getPanelY() + GuiDHDButton.GlyphHeight,
					this.addressMemoization[i],
					0.35f, 0.35f, 0.35f, 1);
		}
		super.drawScreen(par1, par2, par3);
	}
}