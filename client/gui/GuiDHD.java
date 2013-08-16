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
	public int panelWidth = 256;
	public int panelHeight = 256;
	public int panelScale = 2;
	public int panelX;
	public int panelY;
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	@Override
	public void initGui() {
		this.controlList.clear();
		//this.controlList.add(new GuiButton(0, this.width / 2 - 100, (this.height / 4) * 3, "Done"));
		this.panelX = 0;//(this.width / 2) - (panelWidth / 2 / panelScale);
		this.panelY = 0;//(this.height / 2) - (panelHeight / 2 / panelScale);
		this.controlList.add(new GuiDHDButton(panelX, panelY));
	}
	
	@Override
	public void actionPerformed(GuiButton guiButton) {
		if (guiButton.enabled) {
			if (guiButton.id == 0) {
				this.mc.displayGuiScreen((GuiScreen)null);
			}
		}
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		this.drawDefaultBackground();
		
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBindTexture(GL11.GL_TEXTURE_2D, mc.renderEngine.getTexture(CommonProxy.GLYPHS_PNG));
		this.drawTexturedModalRectWithScale(panelX, panelY, 0, 0, panelWidth, panelHeight, panelScale);
		
		super.drawScreen(par1, par2, par3);
	}
	
	public void drawTexturedModalRectWithScale(int screenX, int screenY, int u, int v, int textureWidth, int textureHeight, int scale) {
		float var7 = 0.00390625F * scale;
		float var8 = 0.00390625F * scale;
		int width = textureWidth / scale;
		int height = textureHeight / scale;
		Tessellator var9 = Tessellator.instance;
		var9.startDrawingQuads();
		var9.addVertexWithUV((double) (screenX + 0), (double) (screenY + height), (double) this.zLevel, (double) ((float) (u + 0) * var7), (double) ((float) (v + height) * var8));
		var9.addVertexWithUV((double) (screenX + width), (double) (screenY + height), (double) this.zLevel, (double) ((float) (u + width) * var7), (double) ((float) (v + height) * var8));
		var9.addVertexWithUV((double) (screenX + width), (double) (screenY + 0), (double) this.zLevel, (double) ((float) (u + width) * var7), (double) ((float) (v + 0) * var8));
		var9.addVertexWithUV((double) (screenX + 0), (double) (screenY + 0), (double) this.zLevel, (double) ((float) (u + 0) * var7), (double) ((float) (v + 0) * var8));
		var9.draw();
	}
}