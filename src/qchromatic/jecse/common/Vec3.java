package qchromatic.jecse.common;

public final class Vec3 {
	public float x;
	public float y;
	public float z;

	public Vec3 (Vec3 other) {
		if (other == null) return;

		x = other.x;
		y = other.y;
		z = other.z;
	}

	public Vec3 () { this(0f); }
	public Vec3 (float xyz) { this(xyz, xyz, xyz); }
	public Vec3 (float x, float y) { this(x, y, 0f); }
	public Vec3 (float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vec3 add (Vec3 other) {
		if (other == null) return this;

		x += other.x;
		y += other.y;
		z += other.z;

		return this;
	}

	public Vec3 added (Vec3 other) {
		if (other == null) return new Vec3(this);

		return new Vec3(x + other.x, y + other.y, z + other.z);
	}

	public Vec3 mul (float scalar) {
		x *= scalar;
		y *= scalar;
		z *= scalar;

		return this;
	}

	public Vec3 multiplied (float scalar) {
		return new Vec3(x * scalar, y * scalar, z * scalar);
	}

	public float length () {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	public float lenghtSquared () {
		return x * x + y * y + z * z;
	}

	public Vec3 normalize () {
		float len = length();
		if (len > 0) {
			return mul(1f / len);
		}

		return this;
	}

	public Vec3 normalized () {
		float len = length();
		if (len > 0) {
			return multiplied(1f / len);
		}

		return new Vec3(this);
	}

	public float dot (Vec3 other) {
		if (other == null) return 0f;

		return x * other.x + y * other.y + z * other.z;
	}
}
