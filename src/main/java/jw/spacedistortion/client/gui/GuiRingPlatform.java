package jw.spacedistortion.client.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import jw.spacedistortion.Pair;
import jw.spacedistortion.common.CommonProxy;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;

public class GuiRingPlatform extends GuiScreen {
	public static ResourceLocation backgroundTexture = new ResourceLocation(CommonProxy.MOD_ID + ":" + "textures/gui/rings.png");
	
	public int x;
	public int y;
	public int z;
	
	public GuiRingPlatform(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	@Override
	public void initGui() {
	}
	
	protected void drawPoint(double x, double y) {
		Tessellator t = Tessellator.instance;
		t.addVertex(x,     y,     0);
		t.addVertex(x,     y + 1, 0);
		t.addVertex(x + 1, y + 1, 0);
		t.addVertex(x + 1, y,     0);
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		this.drawDefaultBackground();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glPushMatrix();
		GL11.glTranslatef(this.width / 2, this.height / 2, 0);
		mc.getTextureManager().bindTexture(this.backgroundTexture);
		this.drawTexturedModalRect(-128, -128, 0, 0, 256, 256);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		Tessellator t = Tessellator.instance;
		GL11.glScalef(4, 4, 1);
		GL11.glTranslatef(-16, -16, 0);
		t.startDrawingQuads();
		t.setColorOpaque(255, 255, 255);
		this.drawPoint(0, 0);
		t.draw();
		GL11.glPopMatrix();
		super.drawScreen(par1, par2, par3);
	}
}
