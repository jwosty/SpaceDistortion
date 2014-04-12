package jw.spacedistortion.common.tileentity;

public enum StargateControllerStateOld {
	NO_CONNECTED_STARGATE(0), READY(1), ACTIVE_OUTGOING(2), ACTIVE_INCOMING(3);
	
	private int value;
	StargateControllerStateOld(int value) {
		this.value = value;
	}
	
	public int value() {
		return this.value;
	}
}
