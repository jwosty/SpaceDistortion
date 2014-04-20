package jw.spacedistortion.common.tileentity;

import jw.spacedistortion.common.block.Structure;
import net.minecraft.nbt.NBTTagCompound;

// Pretty much just emulating F#'s discriminated unions
public abstract class StargateControllerState {	
	protected StargateControllerState() { }
	
	public abstract float getGuiDisplayRed();
	public abstract float getGuiDisplayGreen();
	public abstract float getGuiDisplayBlue();
	
	public abstract String getType();
	
	/** Writes the state into an NBT tag */
	public void writeToNBT(NBTTagCompound tag) {
		String type = this.getType();
		tag.setString("type", type);
	}
	
	/** Reads the NBT tag into a state */
	protected void readFromNBT(NBTTagCompound tag) { }
	
	/** Creates a stargate controller state based on the type string given */
	private static StargateControllerState allocateStateType(String type) {
		if (type.equals("invalid")) {
			return new StargateControllerInvalid();
		} else if (type.equals("ready")) {
			return new StargateControllerReady();
		} else if (type.equals("active")) {
			return new StargateControllerActive();
		} else {
			String t = type == null ? "null" : "`" + type + "`";
			throw new RuntimeException("Bad stargate controller state type; probably a bug! (got " + t + ")");
		}
	}
	
	/** Constructs a new stargate state from the NBT tag */
	public static StargateControllerState createFromNBT(NBTTagCompound tag) {
		StargateControllerState state = StargateControllerState.allocateStateType(tag.getString("type"));
		state.readFromNBT(tag);
		return state;
	}
	
	/** Signifies a controller that isn't connected to a stargate ring */
	public static final class StargateControllerInvalid extends StargateControllerState {
		public StargateControllerInvalid() { }

		@Override
		public String getType() {
			return "invalid";
		}

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
		
		protected StargateControllerReady() { }
		
		public StargateControllerReady(byte[] dialingAddress, int currentGlyph) {
			this.addressBuffer = dialingAddress;
			this.currentGlyphIndex = currentGlyph;
		}

		@Override
		public String getType() {
			return "ready";
		}
		
		@Override
		public void writeToNBT(NBTTagCompound tag) {
			super.writeToNBT(tag);
			tag.setByteArray("address", this.addressBuffer);
			tag.setInteger("glyph", this.currentGlyphIndex);
		}
		
		@Override
		protected void readFromNBT(NBTTagCompound tag) {
			this.addressBuffer = tag.getByteArray("address");
			this.currentGlyphIndex = tag.getInteger("glyph");
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
		
		protected StargateControllerActive() { };
		
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
		public void writeToNBT(NBTTagCompound tag) {
			super.writeToNBT(tag);
			tag.setBoolean("incoming", this.isOutgoing);
			tag.setInteger("connx", this.connectedXCoord);
			tag.setInteger("conny", this.connectedYCoord);
			tag.setInteger("connz", this.connectedZCoord);
			tag.setInteger("connd", this.connectedDimension);
		}
		
		@Override
		protected void readFromNBT(NBTTagCompound tag) {
			this.isOutgoing = tag.getBoolean("incoming");
			this.connectedXCoord = tag.getInteger("connx");
			this.connectedYCoord = tag.getInteger("conny");
			this.connectedZCoord = tag.getInteger("connz");
			this.connectedDimension = tag.getInteger("connd");
		}

		@Override
		public float getGuiDisplayRed() {
			return this.isOutgoing ? 1f : 0f;
		}

		@Override
		public float getGuiDisplayGreen() {
			return this.isOutgoing ? 0.25f : 0f;
		}

		@Override
		public float getGuiDisplayBlue() {
			return this.isOutgoing ? 0f : 1f;
		}
	}
}