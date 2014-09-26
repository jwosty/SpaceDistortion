package jw.spacedistortion.client.gui;

import jw.spacedistortion.common.CommonProxy;
import jw.spacedistortion.common.SpaceDistortion;
import jw.spacedistortion.common.block.SDBlock;
import jw.spacedistortion.common.block.Structure;
import jw.spacedistortion.common.network.ChannelHandler;
import jw.spacedistortion.common.network.packet.PacketActivateTransporterRings;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.MathHelper;
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
	
	private void addRings(int x, int y, int z, boolean isThis) {
		int pixelX = (x - this.x);
		int pixelY = (z - this.z);
		this.buttonList.add(new GuiRingPlatformButton((this.width / 2) + (pixelX * 2), (this.height / 2) + (pixelY * 2), x, y, z, isThis));
	}
	
	@Override
	public void initGui() {
		this.buttonList.clear();
		boolean[][] hasFound = new boolean[64][64];
		this.addRings(this.x, this.y, this.z, true);
		for (int rx = -33; rx < 31; rx++) {
			for (int rz = -33; rz < 32; rz++) {
				for (int y = 0; y < 256; y++) {
					int x = this.x + rx;
					int z = this.z + rz;
					if (world.getBlock(x, y, z) == SDBlock.ringPlatform) {
						Structure rings = Structure.detectStructure(
								world, x, y, z, SpaceDistortion.transporterRingsShape,
								SpaceDistortion.templateBlockInfo, ForgeDirection.UP);
						if (rings != null) {
							int scaledX = MathHelper.clamp_int(this.x - rings.x + 64, -64, 64);
							int scaledZ = MathHelper.clamp_int(this.z - rings.z + 64, -64, 64);
							if (!hasFound[scaledX][scaledZ] && !(rings.x == this.x && rings.z == this.z)) {
								this.addRings(rings.x, rings.y, rings.z, rings.x == this.x && rings.y == this.y && rings.z == this.z);
								hasFound[scaledX][scaledZ] = true;
							}
						}
					}
				}
			}
		}
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		GuiRingPlatformButton b = (GuiRingPlatformButton) button;
		if (!b.isThis) {
			ChannelHandler.clientSendPacket(new PacketActivateTransporterRings(this.x, this.y, this.z, b.ringX, b.ringY, b.ringZ));
		}
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
		
		GL11.glPopMatrix();
		super.drawScreen(par1, par2, par3);
	}
}
