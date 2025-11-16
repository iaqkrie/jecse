package qchromatic.jecse.common;

public final class Vec2 {
	public float x;
	public float y;

	public Vec2 (Vec2 other) {
		if (other == null) return;

		x = other.x;
		y = other.y;
	}

	public Vec2 () { this(0f); }
	public Vec2 (float xy) { this(xy, xy); }
	public Vec2 (float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vec2 add (Vec2 other) {
		if (other == null) return this;

		x += other.x;
		y += other.y;

		return this;
	}

	public Vec2 added (Vec2 other) {
		if (other == null) return new Vec2(this);

		return new Vec2(x + other.x, y + other.y);
	}

	public Vec2 mul (float scalar) {
		x *= scalar;
		y *= scalar;

		return this;
	}

	public Vec2 multiplied (float scalar) { return new Vec2(x * scalar, y * scalar); }

	public float length () { return (float) Math.sqrt(x * x + y * y); }

	public float lengthSquared () { return x * x + y * y; }

	public Vec2 normalize () {
		float len = length();
		if (len > 0) {
			return mul(1f / len);
		}

		return this;
	}

	public Vec2 normalized () {
		float len = length();
		if (len > 0) {
			return multiplied(1f / len);
		}

		return new Vec2(this);
	}

	public float dot (Vec2 other) {
		if (other == null) return 0f;

		return x * other.x + y * other.y;
	}
}
