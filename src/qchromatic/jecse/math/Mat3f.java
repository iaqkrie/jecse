package qchromatic.jecse.math;

public class Mat3f {
	public static final int SIZE = 3;
	public static final float[] UNIT_MATRIX = new float[] {
			1f, 0f, 0f,
			0f, 1f, 0f,
			0f, 0f, 1f
	};

	private float[] _matrix;

	public Mat3f () { this(UNIT_MATRIX); }
	public Mat3f (float[] matrix) { _matrix = matrix.clone(); }

	public float[] getMatrix () { return _matrix.clone(); }

	public float get (int x, int y) { return _matrix[y * SIZE + x]; }

	public void set (int x, int y, float value) { _matrix[y * SIZE + x] = value; }

	public Mat3f mul (Mat3f other) {
		float[] newMatix = new float[SIZE * SIZE];

		for (int row = 0; row < SIZE; row++) {
			for (int col = 0; col < SIZE; col++) {
				newMatix[row * SIZE + col] = get(0, row) * other.get(col, 0) +
						                     get(1, row) * other.get(col, 1) +
						                     get(2, row) * other.get(col, 2);
			}
		}

		_matrix = newMatix;
		return this;
	}

	public static Mat3f ortho (float left, float right, float bottom, float top) {
		return new Mat3f(new float[] {
				2 / (right - left), 0,                  0,
				0,                  2 / (top - bottom), 0,
				0,                  0,                  1
		});
	}
}
