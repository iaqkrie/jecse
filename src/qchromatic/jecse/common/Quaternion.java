package qchromatic.jecse.common;

public class Quaternion {
	public float x;
	public float y;
	public float z;
	public float w;

	// region ctors
	public Quaternion (Quaternion other) {
		if (other == null) return;

		x = other.x;
		y = other.y;
		z = other.z;
		w = other.w;
	}

	public Quaternion () {
		identity();
	}

	public Quaternion (float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}
	// endregion

	public Quaternion identity () {
		x = 0f;
		y = 0f;
		z = 0f;
		w = 1f;

		return this;
	}

	public Quaternion normalize() {
		float len = (float) Math.sqrt(x*x + y*y + z*z + w*w);
		if (len > 0) {
			x /= len;
			y /= len;
			z /= len;
			w /= len;
		}
		return this;
	}

	public static Quaternion euler (float pitch, float yaw, float roll) {
		float degToRad = (float) (Math.PI / 180.0 * 0.5);
		float sp = (float) Math.sin(pitch * degToRad);
		float cp = (float) Math.cos(pitch * degToRad);
		float sy = (float) Math.sin(yaw * degToRad);
		float cy = (float) Math.cos(yaw * degToRad);
		float sr = (float) Math.sin(roll * degToRad);
		float cr = (float) Math.cos(roll * degToRad);

		Quaternion q = new Quaternion();
		q.x = cr * sp * cy - sr * cp * sy;
		q.y = sr * cp * cy + cr * sp * sy;
		q.z = sr * sp * cy + cr * cp * sy;
		q.w = cr * cp * cy - sr * sp * sy;

		return q.normalize();
	}

	public Mat4 toMatrix () {
		float xx = x * x, yy = y * y, zz = z * z;
		float xy = x * y, xz = x * z, xw = x * w;
		float yz = y * z, yw = y * w, zw = z * w;

		Mat4 result = new Mat4();
		result.set(0, 0, 1 - 2 * (yy + zz));
		result.set(1, 0, 2 * (xy + zw));
		result.set(2, 0, 2 * (xz - yw));

		result.set(0, 1, 2 * (xy - zw));
		result.set(1, 1, 1 - 2 * (xx + zz));
		result.set(2, 1, 2 * (yz + xw));

		result.set(0, 2, 2 * (xz + yw));
		result.set(1, 2, 2 * (yz - xw));
		result.set(2, 2, 1 - 2 * (xx + yy));

		result.set(3, 3, 1f);

		return result;
	}
}
