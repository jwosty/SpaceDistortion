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
