package qchromatic.jecse.engine;

public class Vec2f {
	public float x;
	public float y;

	public Vec2f () { this(0f); }
	public Vec2f (float xy) { this(xy, xy); }
	public Vec2f (Vec2 other) { this(other.x, other.y); }
	public Vec2f (Vec2f other) { this(other.x, other.y); }
	public Vec2f (float x, float y) {
		this.x = x;
		this.y = y;
	}

	public Vec2f invert () {
		x = -x;
		y = -y;
		return this;
	}

	public Vec2f add (Vec2 other) { return add(new Vec2f(other)); }
	public Vec2f add (Vec2f other) {
		x += other.x;
		y += other.y;
		return this;
	}

	public Vec2f mul (Vec2 other) { return mul(new Vec2f(other)); }
	public Vec2f mul (Vec2f other) {
		x *= other.x;
		y *= other.y;
		return this;
	}

	public Vec2f div (Vec2 other) { return div(new Vec2f(other)); }
	public Vec2f div (Vec2f other) {
		x /= other.x;
		y /= other.y;
		return this;
	}

	public Vec2f scale (int factor) { return scale((float)factor); }
	public Vec2f scale (float factor) {
		x *= factor;
		y *= factor;
		return this;
	}

	public Vec2f pow (float p) {
		x = (float)Math.pow(x, p);
		y = (float)Math.pow(y, p);
		return this;
	}
}
