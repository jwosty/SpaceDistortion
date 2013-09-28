package jw.spacedistortion;

public class Pair<X, Y> {
	public X X;
	public Y Y;
	public Pair(X X, Y Y) {
		this.X = X;
		this.Y = Y;
	}
	
	public String toString() {
		return "(" + this.X + ", " + this.Y + ")";
	}
}
