package qchromatic.jecse.engine;

public class Vec2 {
	public int x;
	public int y;

	public Vec2 () { this(0); }
	public Vec2 (int xy) { this(xy, xy); }
	public Vec2 (Vec2f other) { this((int)(other.x), (int)(other.y)); }
	public Vec2 (Vec2 other) { this(other.x, other.y); }
	public Vec2 (int x, int y) {
		this.x = x;
		this.y = y;
	}

	public Vec2 invert () {
		x = -x;
		y = -y;
		return this;
	}

	public Vec2 add (Vec2f other) { return add(new Vec2(other)); }
	public Vec2 add (Vec2 other) {
		x += other.x;
		y += other.y;
		return this;
	}

	public Vec2 mul (Vec2f other) { return mul(new Vec2(other)); }
	public Vec2 mul (Vec2 other) {
		x *= other.x;
		y *= other.y;
		return this;
	}

	public Vec2 div (Vec2f other) { return div(new Vec2(other)); }
	public Vec2 div (Vec2 other) {
		x /= other.x;
		y /= other.y;
		return this;
	}

	public Vec2 scale (float factor) { return scale((int)factor); }
	public Vec2 scale (int factor) {
		x *= factor;
		y *= factor;
		return this;
	}

	public Vec2 pow (int p) {
		x = (int)Math.pow(x, p);
		y = (int)Math.pow(y, p);
		return this;
	}
}
