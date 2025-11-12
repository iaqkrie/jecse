package qchromatic.jecse.common;

public class Mat4 {
	private float[] _matrix;

	// region ctors
	public Mat4 (float[] matrix) {
		_matrix = matrix.clone();
	}

	public Mat4 () {
		_matrix = new float[16];
		identity();
	}
	// endregion

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

	public Mat4 mul (Mat4 other) {
		float[] result = new float[16];

		for (int i = 0; i < 4; i ++) {
			for (int j = 0; j < 4; j++) {
				float sum = 0f;
				for (int k = 0; k < 4; k++)
					sum += get(k, i) * other.get(j, k);

				result[i * 4 + j] = sum;
			}
		}

		_matrix = result;
		return this;
	}

	public static Mat4 translation (Vec3 position) {
		return translation(position.x, position.y, position.z);
	}
	public static Mat4 translation (float x, float y, float z) {
		Mat4 result = new Mat4();
		result.set(3, 0, x);
		result.set(3, 1, y);
		result.set(3, 2, z);
		return result;
	}

	public static Mat4 rotation (Vec3 rotation) {
		return rotation(rotation.x, rotation.y, rotation.z);
	}
	public static Mat4 rotation (float x, float y, float z) {
		return rotation(Quaternion.euler(x, y, z));
	}
	public static Mat4 rotation (Quaternion rotation) {
		return rotation.toMatrix();
	}

	public static Mat4 scale (Vec3 scale) {
		return scale(scale.x, scale.y, scale.z);
	}
	public static Mat4 scale (float x, float y, float z) {
		Mat4 result = new Mat4();
		result.set(0, 0, x);
		result.set(1, 1, y);
		result.set(2, 2, z);
		return result;
	}
}
