package qchromatic.jecse.math;

/**
 * 3x3 float matrix with <b>COLUMN-MAJOR</b> layout
 */
public class Mat3f {
	private final float[] _matrix;

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

	public float get (int x, int y) { return _matrix[x * 3 + y]; }

	public void set (int x, int y, float value) { _matrix[x * 3 + y] = value; }

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

	public static Mat3f ortho (float left, float right, float bottom, float top) {
		return new Mat3f(new float[] {
				2 / (right - left), 0,                  0,
				0,                  2 / (top - bottom), 0,
				0,                  0,                  1
		});
	}
}
