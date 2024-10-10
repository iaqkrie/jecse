package qchromatic.jecse.math;

public class Mat3f {
	private float[] _matrix;

	public Mat3f () {
		_matrix = new float[9];

		_matrix[0] = 1;
		_matrix[4] = 1;
		_matrix[8] = 1;
	}

	public float[] getMatrix () {
		return _matrix.clone();
	}

	public float get (int x, int y) {
		return _matrix[y * 3 + x]; // TODO
	}

	public void set (int x, int y, float value) {
		_matrix[y * 3 + x] = value; // TODO
	}

	public void translate (float x, float y) {
		set(2, 0, get(2, 0) + x);
		set(2, 1, get(2, 1) + y);
	}

	public void setTranslate (float x, float y) {
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
}
