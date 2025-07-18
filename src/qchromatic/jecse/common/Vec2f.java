package qchromatic.jecse.common;

public class Vec2f {
	public float x;
	public float y;

	public Vec2f (Vec2f other) {
		if (other == null) return;

		x = other.x;
		y = other.y;
	}

	public Vec2f () { this(0f); }
	public Vec2f (float xy) { this(xy, xy); }
	public Vec2f (float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vec2f add (Vec2f other) {
		if (other == null) return this;

		x += other.x;
		y += other.y;

		return this;
	}

	public Vec2f mul (float scalar) {
		x *= scalar;
		y *= scalar;

		return this;
	}
}
