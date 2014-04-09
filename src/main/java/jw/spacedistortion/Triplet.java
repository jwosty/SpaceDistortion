package jw.spacedistortion;

public class Triplet<X, Y, Z> {
	public X X;
	public Y Y;
	public Z Z;
	public Triplet(X X, Y Y, Z Z) {
		this.X = X;
		this.Y = Y;
		this.Z = Z;
	}

	public String toString() {
		return "(" + this.X + ", " + this.Y + ", " + this.Z + ")";
	}
}
