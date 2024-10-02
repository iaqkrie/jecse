package qchromatic.jecse.engine;

public class Vec2f {
	public float x;
	public float y;

	public Vec2f (float x, float y) {
		this.x = x;
		this.y = y;
	}
	public Vec2f (float xy) {
		this(xy, xy);
	}
	public Vec2f () {
		this(0);
	}

	public Vec2f (Vec2f other) {
		this(other.x, other.y);
	}
	public Vec2f (Vec2 other) {
		this(other.x, other.y);
	}
}
