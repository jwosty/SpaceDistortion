package jw.spacedistortion;

public enum Axis {
	X, Y, Z;
	
	public int toInt() {
		if (this == X) {
			return 0;
		} else if (this == Y) {
			return 1;
		} else {
			return 2;
		}
	}
	
	/**
	 * Calculates the pitch and yaw values
	 * @return A pair containing the pitch and yaw, in that order
	 */
	public Pair<Integer, Integer> getPitchAndYaw() {
		if (this == X) {
			return new Pair(0, -90);
		} else if (this == Y) {
			return new Pair(-90, 0);
		} else {
			return new Pair(0, 0);
		}
	}
	
	public static Axis ofInt(int x) {
		if (x == 0) {
			return X;
		} else if (x == 1) {
			return Y;
		} else {
			return Z;
		}
	}
}
