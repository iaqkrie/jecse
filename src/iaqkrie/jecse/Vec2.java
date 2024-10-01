package iaqkrie.jecse;

public class Vec2 {
	public int x;
	public int y;

	public Vec2 (int x, int y) {
		this.x = x;
		this.y = y;
	}
	public Vec2 (int xy) {
		this(xy, xy);
	}
	public Vec2 () {
		this(0);
	}

	public Vec2 (Vec2 other) {
		this(other.x, other.y);
	}
	public Vec2 (Vec2f other) {
		this((int)(other.x), (int)(other.y));
	}
}
