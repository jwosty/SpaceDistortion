package jw.spacedistortion.common.tileentity;

import net.minecraft.nbt.NBTTagCompound;

// Pretty much just emulating F#'s discriminated unions
public abstract class StargateControllerState {	
	private StargateControllerState() { }
	
	public abstract float getGuiDisplayRed();
	public abstract float getGuiDisplayGreen();
	public abstract float getGuiDisplayBlue();
	
	public abstract String getType();
	
	public abstract void writeNBTData(NBTTagCompound tag);
	
	public void writeToNBT(NBTTagCompound tag) {
		String type = this.getType();
		tag.setString("type", type);
		this.writeNBTData(tag);
	}
	
	
	/** Signifies a controller that isn't connected to a stargate ring */
	public static final class StargateControllerInvalid extends StargateControllerState {
		public StargateControllerInvalid() { }

		@Override
		public String getType() {
			return "invalid";
		}

		@Override
		public void writeNBTData(NBTTagCompound tag) { }

		@Override
		public float getGuiDisplayRed() {
			return 0;
		}

		@Override
		public float getGuiDisplayGreen() {
			return 0;
		}

		@Override
		public float getGuiDisplayBlue() {
			return 0;
		}
	}
	
	/** Signifies a controller that is standing by (connected to a ring but not currently active) */
	public static final class StargateControllerReady extends StargateControllerState {
		public byte[] addressBuffer;
		public int currentGlyphIndex;
		
		public StargateControllerReady(byte[] dialingAddress, int currentGlyph) {
			this.addressBuffer = dialingAddress;
			this.currentGlyphIndex = currentGlyph;
		}

		@Override
		public String getType() {
			return "ready";
		}
		
		@Override
		public void writeNBTData(NBTTagCompound tag) {
			tag.setByteArray("address", this.addressBuffer);
			tag.setInteger("glyph", this.currentGlyphIndex);
		}

		@Override
		public float getGuiDisplayRed() {
			return 1f;
		}

		@Override
		public float getGuiDisplayGreen() {
			return 0.5f;
		}

		@Override
		public float getGuiDisplayBlue() {
			return 0f;
		}
	}
	
	/** Signifies a controller that is activated (connected to anther stargate) */
	public static final class StargateControllerActive extends StargateControllerState {
		public boolean isOutgoing;
		public int connectedXCoord;
		public int connectedYCoord;
		public int connectedZCoord;
		public int connectedDimension;
		
		public StargateControllerActive(boolean isIncoming, int connectedXCoord, int connectedYCoord,
				int connectedZCoord, int connectedDimension) {
			this.isOutgoing = isIncoming;
			this.connectedXCoord = connectedXCoord;
			this.connectedYCoord = connectedYCoord;
			this.connectedZCoord = connectedZCoord;
			this.connectedDimension = connectedDimension;
		}

		@Override
		public String getType() {
			return "active";
		}

		@Override
		public void writeNBTData(NBTTagCompound tag) {
			tag.setBoolean("incoming", this.isOutgoing);
			tag.setInteger("connx", this.connectedXCoord);
			tag.setInteger("conny", this.connectedYCoord);
			tag.setInteger("connz", this.connectedZCoord);
			tag.setInteger("connd", this.connectedDimension);
		}

		@Override
		public float getGuiDisplayRed() {
			return 1f;
		}

		@Override
		public float getGuiDisplayGreen() {
			return 0f;
		}

		@Override
		public float getGuiDisplayBlue() {
			return 0f;
		}
	}
	
	public static StargateControllerState readFromNBT(NBTTagCompound tag) {
		String type = tag.getString("type");
		switch (type) {
		case "invalid":
			return new StargateControllerInvalid();
		case "ready":
			return new StargateControllerReady(tag.getByteArray("address"), tag.getInteger("glyph"));
		case "active":
			return new StargateControllerActive(tag.getBoolean("incoming"),
					tag.getInteger("connx"), tag.getInteger("conny"), tag.getInteger("connz"), tag.getInteger("connd"));
		default:
			String t = type == null ? "null" : "`" + type + "`";
			throw new RuntimeException("Bad stargate controller state type; probably a bug! (got " + t + ")");
		}
	}
}