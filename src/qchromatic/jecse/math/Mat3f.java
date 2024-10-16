package qchromatic.jecse.math;

/**
 * 3x3 float matrix with <b>COLUMN-MAJOR</b> layout
 */
public class Mat3f {
	public static final int SIZE = 3;

	private float[] _matrix;

	public Mat3f () {
		_matrix = new float[] {
				1f, 0f, 0f,
				0f, 1f, 0f,
				0f, 0f, 1f
		};
	}
	public Mat3f (float[] matrix) {
		_matrix = matrix.clone();
	}

	public float[] getMatrix () { return _matrix.clone(); }

	public float get (int x, int y) { return _matrix[x * SIZE + y]; }

	public void set (int x, int y, float value) { _matrix[x * SIZE + y] = value; }

	public void mul (Mat3f other) {
		float[] newMatix = new float[SIZE * SIZE];

		for (int row = 0; row < SIZE; row++) {
			for (int col = 0; col < SIZE; col++) {
				newMatix[col * SIZE + row] = get(0, row) * other.get(col, 0) +
						                     get(1, row) * other.get(col, 1) +
						                     get(2, row) * other.get(col, 2);
			}
		}

		_matrix = newMatix;
	}

	public void translate (float x, float y) {
		set(2, 0, get(2, 0) + x);
		set(2, 1, get(2, 1) + y);
	}

	public void setTranslation (float x, float y) {
		set (2, 0, x);
		set (2, 1, y);
	}

	public void scale (float x, float y) {
		set(0, 0, get(0, 0) * x);
		set(1, 1, get(1, 1) * y);
	}

	public void setScale (float x, float y) {
		set(0, 0, x);
		set(1, 1, y);
	}

	public void setRotation (float angle) {
		float theta = (float) Math.toRadians(angle);
		float sin = (float) Math.sin(theta);
		float cos = (float) Math.cos(theta);

		Mat3f rotationMatrix = new Mat3f(new float[] {
				 cos, sin, 0,
				-sin, cos, 0,
				 0,   0,   1
		});

		this.mul(rotationMatrix);
	}

	public static Mat3f ortho (float left, float right, float bottom, float top) {
		return new Mat3f(new float[] {
				2 / (right - left), 0,                  0,
				0,                  2 / (top - bottom), 0,
				0,                  0,                  1
		});
	}
}
