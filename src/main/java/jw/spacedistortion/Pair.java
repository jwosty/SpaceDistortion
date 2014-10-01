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
	
	public boolean equals(Object obj) {
		return (obj instanceof Pair) && this.X == ((Pair) obj).X && this.Y == ((Pair) obj).Y;
	}
}
