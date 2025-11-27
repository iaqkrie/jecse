package qchromatic.jecse.common;

public final class Quaternion {
	public float w;
	public float x;
	public float y;
	public float z;

	public Quaternion(Quaternion other) {
		if (other == null) return;

		this.w = other.w;
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
	}

	public Quaternion() { this(1f, 0f, 0f, 0f); }
	public Quaternion(float w, float x, float y, float z) {
		this.w = w;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Quaternion identity() {
		w = 1f;
		x = 0f;
		y = 0f;
		z = 0f;

		return this;
	}

	public static Quaternion axisAngle(Vec3 axis, float angleDeg) {
		float angleRad = (float) Math.toRadians(angleDeg);
		float halfAngle = angleRad / 2f;

		float s = (float) Math.sin(halfAngle);
		float c = (float) Math.cos(halfAngle);

		return new Quaternion(
				c,
				axis.x * s,
				axis.y * s,
				axis.z * s
		);
	}

	public static Quaternion euler (Vec3 eulerDegrees) {
		return euler(eulerDegrees.x, eulerDegrees.y, eulerDegrees.z);
	}

	public static Quaternion euler (float pitch, float yaw, float roll) {
		float pitchRad = (float) Math.toRadians(pitch) / 2f;
		float yawRad   = (float) Math.toRadians(yaw) / 2f;
		float rollRad  = (float) Math.toRadians(roll) / 2f;

		float sinPitch = (float) Math.sin(pitchRad);
		float cosPitch = (float) Math.cos(pitchRad);
		float sinYaw   = (float) Math.sin(yawRad);
		float cosYaw   = (float) Math.cos(yawRad);
		float sinRoll  = (float) Math.sin(rollRad);
		float cosRoll  = (float) Math.cos(rollRad);

		float w = cosRoll * cosPitch * cosYaw + sinRoll * sinPitch * sinYaw;
		float x = cosRoll * sinPitch * cosYaw + sinRoll * cosPitch * sinYaw;
		float y = cosRoll * cosPitch * sinYaw - sinRoll * sinPitch * cosYaw;
		float z = sinRoll * cosPitch * cosYaw - cosRoll * sinPitch * sinYaw;

		return new Quaternion(w, x, y, z);
	}

	public Quaternion mul(Quaternion other) {
		if (other == null) return this;

		float newW = this.w * other.w - this.x * other.x - this.y * other.y - this.z * other.z;
		float newX = this.w * other.x + this.x * other.w + this.y * other.z - this.z * other.y;
		float newY = this.w * other.y - this.x * other.z + this.y * other.w + this.z * other.x;
		float newZ = this.w * other.z + this.x * other.y - this.y * other.x + this.z * other.w;

		this.w = newW;
		this.x = newX;
		this.y = newY;
		this.z = newZ;

		return this;
	}

	public Quaternion multiplied(Quaternion other) {
		return new Quaternion(this).mul(other);
	}

	public Quaternion conjugate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;

		return this;
	}

	public Quaternion conjugated() {
		return new Quaternion(this.w, -this.x, -this.y, -this.z);
	}

	public Quaternion normalize() {
		float length = length();

		if (length == 0) return identity();

		this.w /= length;
		this.x /= length;
		this.y /= length;
		this.z /= length;

		return this;
	}

	public Quaternion normalized() {
		return new Quaternion(this).normalize();
	}

	public float length() {
		return (float) Math.sqrt(w * w + x * x + y * y + z * z);
	}

	public float lengthSquared() {
		return w * w + x * x + y * y + z * z;
	}

	public Vec3 rotate(Vec3 v) {
		if (v == null) return new Vec3();

		float ix =  w * v.x + y * v.z - z * v.y;
		float iy =  w * v.y + z * v.x - x * v.z;
		float iz =  w * v.z + x * v.y - y * v.x;
		float iw = -x * v.x - y * v.y - z * v.z;

		float resultX = ix * w + iw * -x + iy * -z - iz * -y;
		float resultY = iy * w + iw * -y + iz * -x - ix * -z;
		float resultZ = iz * w + iw * -z + ix * -y - iy * -x;

		return new Vec3(resultX, resultY, resultZ);
	}

	public Vec3 forward() {
		return rotate(new Vec3(0f, 0f, -1f));
	}

	public Vec3 right() {
		return rotate(new Vec3(1f, 0f, 0f));
	}

	public Vec3 up() {
		return rotate(new Vec3(0f, 1f, 0f));
	}

	public Mat4 toMat4() {
		float xx = x * x;
		float yy = y * y;
		float zz = z * z;
		float xy = x * y;
		float xz = x * z;
		float yz = y * z;
		float wx = w * x;
		float wy = w * y;
		float wz = w * z;

		Mat4 result = new Mat4();

		result.set(0, 0, 1f - 2f * (yy + zz));
		result.set(0, 1, 2f * (xy - wz));
		result.set(0, 2, 2f * (xz + wy));
		result.set(0, 3, 0f);
		result.set(1, 0, 2f * (xy + wz));
		result.set(1, 1, 1f - 2f * (xx + zz));
		result.set(1, 2, 2f * (yz - wx));
		result.set(1, 3, 0f);
		result.set(2, 0, 2f * (xz - wy));
		result.set(2, 1, 2f * (yz + wx));
		result.set(2, 2, 1f - 2f * (xx + yy));
		result.set(2, 3, 0f);
		result.set(3, 0, 0f);
		result.set(3, 1, 0f);
		result.set(3, 2, 0f);
		result.set(3, 3, 1f);

		return result;
	}
}
