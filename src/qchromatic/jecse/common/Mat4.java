package qchromatic.jecse.common;

public class Mat4 {
	private float[] _matrix;

	// region ctors
	public Mat4 (float[] matrix) { _matrix = matrix.clone(); }

	public Mat4 () {
		_matrix = new float[16];
		identity();
	}
	// endregion

	public float[] getMatrix () { return _matrix.clone(); }

	public float get (int x, int y) { return _matrix[y * 4 + x]; }
	public void set (int x, int y, float value) { _matrix[y * 4 + x] = value; }

	public Mat4 identity () {
		for (int i = 0; i < 16; i++) _matrix[i] = 0f;

		set(0, 0, 1f);
		set(1, 1, 1f);
		set(2, 2, 1f);
		set(3, 3, 1f);

		return this;
	}

	public Mat4 transpose () {
		Mat4 result = new Mat4();
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				result.set(i, j, get(j, i));
			}
		}

		_matrix = result.getMatrix();
		return this;
	}

	public Mat4 mul (Mat4 other) {
		float[] result = new float[16];

		for (int i = 0; i < 4; i ++) {
			for (int j = 0; j < 4; j++) {
				float sum = 0f;
				for (int k = 0; k < 4; k++)
					sum += get(k, i) * other.get(j, k);

				result[i * 4 + j] = sum; // TODO
			}
		}

		_matrix = result;
		return this;
	}

	public static Mat4 translation (Vec3 position) { return translation(position.x, position.y, position.z); }
	public static Mat4 translation (float x, float y, float z) {
		Mat4 result = new Mat4();
		result.set(3, 0, x);
		result.set(3, 1, y);
		result.set(3, 2, z);
		return result;
	}

	public static Mat4 rotation (Vec3 rotation) { return rotation(rotation.x, rotation.y, rotation.z); }
	public static Mat4 rotation (float x, float y, float z) {
		Mat4 result = new Mat4();
		result
				.mul(rotationX(x))
				.mul(rotationY(y))
				.mul(rotationZ(z));

		return result;
	}

	private static Mat4 rotationX (float angleDeg) {
		float sin = (float) Math.sin(Math.toRadians(angleDeg));
		float cos = (float) Math.cos(Math.toRadians(angleDeg));

		Mat4 result = new Mat4();
		result.set(1, 1, cos);
		result.set(1, 2, -sin);
		result.set(2, 1, sin);
		result.set(2, 2, cos);
		return result;
	}

	private static Mat4 rotationY (float angleDeg) {
		float sin = (float) Math.sin(Math.toRadians(angleDeg));
		float cos = (float) Math.cos(Math.toRadians(angleDeg));

		Mat4 result = new Mat4();
		result.set(0, 0, cos);
		result.set(0, 2, sin);
		result.set(2, 0, -sin);
		result.set(2, 2, cos);
		return result;
	}

	private static Mat4 rotationZ (float angleDeg) {
		float sin = (float) Math.sin(Math.toRadians(angleDeg));
		float cos = (float) Math.cos(Math.toRadians(angleDeg));

		Mat4 result = new Mat4();
		result.set(0, 0, cos);
		result.set(0, 1, -sin);
		result.set(1, 0, sin);
		result.set(1, 1, cos);
		return result;
	}

	public static Mat4 scale (Vec3 scale) { return scale(scale.x, scale.y, scale.z); }
	public static Mat4 scale (float x, float y, float z) {
		Mat4 result = new Mat4();
		result.set(0, 0, x);
		result.set(1, 1, y);
		result.set(2, 2, z);
		return result;
	}

	public static Mat4 frustum (float fovDeg, float aspect, float near, float far) {
		float fovRad = (float) Math.toRadians(fovDeg);
		float f = 1f / (float) Math.tan(fovRad / 2f);

		Mat4 result = new Mat4();
		result.set(0, 0, f / aspect);
		result.set(1, 1, f);
		result.set(2, 2, (far + near) / (near - far));
		result.set(2, 3, -1f);
		result.set(3, 2, (2f * far * near) / (near - far));
		return result;
	}

	public static Mat4 ortho (float orthoSize, float aspect, float near, float far) {
		float width = 2f * orthoSize * aspect;
		float height = 2f * orthoSize;

		Mat4 result = new Mat4();
		result.set(0, 0, 2f / width);
		result.set(1, 1, 2f / height);
		result.set(2, 2, 2f / (near - far));
		result.set(3, 2, (near + far) / (near - far));
		return result;
	}
}
