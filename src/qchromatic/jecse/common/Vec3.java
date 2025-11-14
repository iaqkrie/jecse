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

	public Vec3 multiplied (float scalar) { return new Vec3(x * scalar, y * scalar, z * scalar); }

	public float length () { return (float) Math.sqrt(x * x + y * y + z * z); }

	public float lenghtSquared () { return x * x + y * y + z * z; }

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

	public Vec3 cross(Vec3 other) {
		if (other == null) return this;

		float newX = this.y * other.z - this.z * other.y;
		float newY = this.z * other.x - this.x * other.z;
		float newZ = this.x * other.y - this.y * other.x;

		this.x = newX;
		this.y = newY;
		this.z = newZ;

		return this;
	}

	public Vec3 crossed(Vec3 other) {
		if (other == null) return new Vec3(this);

		return new Vec3(
				this.y * other.z - this.z * other.y,
				this.z * other.x - this.x * other.z,
				this.x * other.y - this.y * other.x
		);
	}

	public float dot (Vec3 other) {
		if (other == null) return 0f;

		return x * other.x + y * other.y + z * other.z;
	}
}
