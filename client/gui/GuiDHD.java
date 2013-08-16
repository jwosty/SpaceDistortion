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
		super.drawScreen(par1, par2, par3);
	}
}