package jw.spacedistortion;

public class Quadruplet<A, B, C, D> {
	public A A;
	public B B;
	public C C;
	public D D;
	public Quadruplet(A A, B B, C C, D D) {
		this.A = A;
		this.B = B;
		this.C = C;
		this.D = D;
	}

	public String toString() {
		return "(" + this.A + ", " + this.B + ", " + this.C + ", " + this.D
				+ ")";
	}
}
