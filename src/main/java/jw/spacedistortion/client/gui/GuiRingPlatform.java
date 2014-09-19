package jw.spacedistortion.client.gui;

import jw.spacedistortion.common.CommonProxy;
import jw.spacedistortion.common.SpaceDistortion;
import jw.spacedistortion.common.block.SDBlock;
import jw.spacedistortion.common.block.Structure;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

public class GuiRingPlatform extends GuiScreen {
	public static ResourceLocation backgroundTexture = new ResourceLocation(CommonProxy.MOD_ID + ":" + "textures/gui/rings.png");
	
	public World world;
	public int x;
	public int y;
	public int z;
	
	public GuiRingPlatform(World world, int x, int y, int z) {
		this.world = world;
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
	private void addRings(int x, int z, boolean isThis) {
		int pixelX = x - this.x;
		int pixelY = z - this.z;
		this.buttonList.add(new GuiRingPlatformButton((this.width / 2) + (pixelX * 4), (this.height / 2) + (pixelY * 4), isThis));
	}
	
	@Override
	public void initGui() {
		for (int x = this.x - 16; x < this.x + 16; x++) {
			for (int z = -16; z < this.z + 16; z++) {
				for (int y = 0; y < 256; y++) {
					if (world.getBlock(x, y, z) == SDBlock.ringPlatform) {
						Structure rings = Structure.detectStructure(
								world, x, y, z, SpaceDistortion.transporterRingsShape,
								SpaceDistortion.templateBlockInfo, ForgeDirection.UP);
						if (rings != null) {
							this.addRings(rings.x, rings.z, rings.x == this.x && rings.y == this.y && rings.z == this.z);
						}
					}
				}
			}
		}
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
		// Put 0, 0 at the center of the screen
		GL11.glTranslatef(this.width / 2, this.height / 2, 0);
		// Render the GUI background
		mc.getTextureManager().bindTexture(this.backgroundTexture);
		this.drawTexturedModalRect(-128, -128, 0, 0, 256, 256);
		
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		Tessellator t = Tessellator.instance;
		// Scale so that the coordinates are in texels
		GL11.glScalef(4, 4, 1);
		GL11.glTranslatef(-16, -16, 0);
		
		GL11.glPopMatrix();
		super.drawScreen(par1, par2, par3);
	}
}
