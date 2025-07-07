package qchromatic.jecse.common;

public class Vec4 {
	public int x;
	public int y;
	public int z;
	public int w;

	public Vec4 (Vec4 other) {
		if (other == null) return;

		x = other.x;
		y = other.y;
		z = other.z;
		w = other.w;
	}

	public Vec4 (Vec2 xy, int z, int w) {
		if (xy == null) return;

		x = xy.x;
		y = xy.y;
		this.z = z;
		this.w = w;
	}
	public Vec4 (Vec3 xyz, int w) {
		if (xyz == null) return;

		x = xyz.x;
		y = xyz.y;
		z = xyz.z;
		this.w = w;
	}

	public Vec4 () { this(0); }
	public Vec4 (int xyzw) { this(xyzw, xyzw, xyzw, xyzw); }
	public Vec4 (int x, int y, int z, int w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Vec4 add (Vec4 other) {
		if (other == null) return this;

		x += other.x;
		y += other.y;
		z += other.z;
		w += other.w;

		return this;
	}
}
