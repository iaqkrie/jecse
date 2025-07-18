package qchromatic.jecse.common;

public class Vec4f {
	public float x;
	public float y;
	public float z;
	public float w;

	public Vec4f (Vec4f other) {
		if (other == null) return;

		x = other.x;
		y = other.y;
		z = other.z;
		w = other.w;
	}

	public Vec4f (Vec2 xy, float z, float w) {
		if (xy == null) return;

		x = xy.x;
		y = xy.y;
		this.z = z;
		this.w = w;
	}
	public Vec4f (Vec3 xyz, float w) {
		if (xyz == null) return;

		x = xyz.x;
		y = xyz.y;
		z = xyz.z;
		this.w = w;
	}

	public Vec4f () { this(0f); }
	public Vec4f (float xyzw) { this(xyzw, xyzw, xyzw, xyzw); }
	public Vec4f (float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Vec4f add (Vec4f other) {
		if (other == null) return this;

		x += other.x;
		y += other.y;
		z += other.z;
		w += other.w;

		return this;
	}

	public Vec4f mul (float scalar) {
		x *= scalar;
		y *= scalar;
		z *= scalar;
		w *= scalar;

		return this;
	}
}
