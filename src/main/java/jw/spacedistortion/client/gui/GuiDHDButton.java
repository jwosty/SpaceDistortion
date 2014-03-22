package jw.spacedistortion.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

public class GuiDHDButton extends GuiButton {
	public static int GlyphWidth = 32;
	public static int GlyphHeight = 32;
	public static int GlyphSheetWidth = 256;
	public static int GlyphSheetHeight = 160;
	public static ResourceLocation glyphTexture;
	public byte glyphID;
	public boolean isActivated = false;
	
	public GuiDHDButton(int x, int y, ResourceLocation glyphTexture, byte glyphID) {
		super(0, x, y, GlyphWidth, GlyphHeight, "");
		this.glyphTexture = glyphTexture;
		this.glyphID = glyphID;
	}
	
	@Override
	public void drawButton(Minecraft mc, int par1, int par2) {
		if (this.visible) {
			if (this.isActivated) {
				// all glyphs are orange, except for the special center button (which is red)
				if (this.glyphID == 39) {
					GL11.glColor4f(1, 0, 0, 1);
				} else {
					GL11.glColor4f(1, 0.5f, 0, 1);
				}
			} else {
				GL11.glColor4f(0.25f, 0.25f, 0.25f, 1);
			}
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glEnable(GL11.GL_BLEND);
			mc.getTextureManager().bindTexture(this.glyphTexture);
			this.drawTexturedModalRect(this.xPosition, this.yPosition,
					this.glyphID % (GlyphSheetWidth / GlyphWidth) * GlyphWidth, this.glyphID / (GlyphSheetWidth / GlyphWidth) * GlyphHeight,
					GlyphWidth, GlyphHeight);
		}
	}
	
	@Override
	public void func_146113_a(SoundHandler soundHandler) {
		EntityPlayer player = Minecraft.getMinecraft().thePlayer;
		/*
		jw.spacedistortion.client.SoundHandler.playSoundAtPosition(
				(World) Minecraft.getMinecraft().theWorld, (int) player.posX,
				(int) player.posY, (int) player.posZ, "");
		*/
	}
}
