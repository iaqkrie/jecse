package qchromatic.jecse.math;

public class Mat2f {
	public static final int SIZE = 2;
	public static final float[] UNIT_MATRIX = new float[] {
			1, 0,
			0, 1
	};

	private float[] _matrix;

	public Mat2f () { this(UNIT_MATRIX); }
	public Mat2f (float[] matrix) { _matrix = matrix.clone(); }

	public float[] getMatrix () { return _matrix.clone(); }

	public float get (int x, int y) { return _matrix[y * SIZE + x]; }

	public void set (int x, int y, float value) { _matrix[y * SIZE + x] = value; }

	public float determinant () {
		return  get(0, 0) * get(1, 1) -
				get(1, 0) * get(0, 1);
	}
}
