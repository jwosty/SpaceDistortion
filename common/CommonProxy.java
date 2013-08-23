package jw.spacedistortion.common;

public class CommonProxy {
	public static final String MOD_ID = "spacedistortion";
	public static final String MOD_NAME = "SpaceDistortion";
	public static final String MOD_VERSION = "0.0.0";
	public static final String TEX_ROOT = "/jw/spacedistortion/texture/";
	public static final String TEXTURES_PNG = TEX_ROOT + "textures.png";
	public static final String GLYPHS_PNG = TEX_ROOT + "glyphs.png";
	// Looks nothing like the DHD in the show
	public static final String DHD_PNG = TEX_ROOT + "DHD.png";
	
	public void registerRenderers() {
		// Nothing here as the server doesn't render graphics!
	}
}
