package qchromatic.jecse.common;

public class Vec3f {
	public float x;
	public float y;
	public float z;

	public Vec3f (Vec3f other) {
		if (other == null) return;

		x = other.x;
		y = other.y;
		z = other.z;
	}

	public Vec3f (Vec2f xy, float z) {
		if (xy == null) return;

		x = xy.x;
		y = xy.y;
		this.z = z;
	}

	public Vec3f () { this(0f); }
	public Vec3f (float xyz) { this(xyz, xyz, xyz); }
	public Vec3f (float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vec3f add (Vec3f other) {
		if (other == null) return this;

		x += other.x;
		y += other.y;
		z += other.z;

		return this;
	}

	public Vec3f mul (float scalar) {
		x *= scalar;
		y *= scalar;
		z *= scalar;

		return this;
	}
}
