package qchromatic.jecse.common;

public final class Vec4 {
	public float x;
	public float y;
	public float z;
	public float w;

	public Vec4 (Vec4 other) {
		if (other == null) return;

		x = other.x;
		y = other.y;
		z = other.z;
		w = other.w;
	}

	public Vec4 () { this(0f); }
	public Vec4 (float xyzw) { this(xyzw, xyzw, xyzw, xyzw); }
	public Vec4 (float x, float y, float z) { this(x, y, z, 0f); }
	public Vec4 (float x, float y, float z, float w) {
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

	public Vec4 added (Vec4 other) {
		if (other == null) return new Vec4(this);

		return new Vec4(x + other.x, y + other.y, z + other.z, w + other.w);
	}

	public Vec4 mul (float scalar) {
		x *= scalar;
		y *= scalar;
		z *= scalar;
		w *= scalar;

		return this;
	}

	public Vec4 multiplied (float scalar) { return new Vec4(x * scalar, y * scalar, z * scalar, w * scalar); }

	public float length () { return (float) Math.sqrt(x * x + y * y + z * z + w * w); }

	public float lengthSquared () { return x * x + y * y + z * z + w * w; }

	public Vec4 normalize () {
		float len = length();
		if (len > 0) {
			return mul(1f / len);
		}

		return this;
	}

	public Vec4 normalized () {
		float len = length();
		if (len > 0) {
			return multiplied(1f / len);
		}

		return new Vec4(this);
	}

	public float dot (Vec4 other) {
		if (other == null) return 0f;

		return x * other.x + y * other.y + z * other.z + w * other.w;
	}
}
