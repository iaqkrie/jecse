package qchromatic.jecse.common;

public class Vec2 {
	public int x;
	public int y;

	public Vec2 (Vec2 other) {
		if (other == null) return;

		x = other.x;
		y = other.y;
	}

	public Vec2 () { this(0); }
	public Vec2 (int xy) { this(xy, xy); }
	public Vec2 (int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Vec2 add (Vec2 other) {
		if (other == null) return this;

		x += other.x;
		y += other.y;

		return this;
	}
}
