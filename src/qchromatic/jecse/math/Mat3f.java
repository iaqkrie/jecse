package qchromatic.jecse.math;

import java.util.ArrayList;
import java.util.List;

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

	public Mat3f transpose () {
		float[] newMatrix = new float[] {
				get(0, 0), get(0, 1), get(0, 2),
				get(1, 0), get(1, 1), get(1, 2),
				get(2, 0), get(2, 1), get(2, 2)
		};

		return new Mat3f(newMatrix);
	}

	public float minor (int x, int y) {
		List<Float> tempMatrixList = new ArrayList<>();
		for (int i = 0; i < SIZE; i++) {
			for (int j = 0; j < SIZE; j++) {
				if (i == y || j == x)
					continue;

				tempMatrixList.add(get(j, i));
			}
		}
		float[] tempMatrixArray = new float[4];
		for (int i = 0; i < tempMatrixArray.length; i++)
			tempMatrixArray[i] = tempMatrixList.get(i);

		return new Mat2f(tempMatrixArray).determinant();
	}

	public float determinant () {
		return  get(0, 0) * get(1, 1) * get(2, 2) +
				get(1, 0) * get(2, 1) * get(0, 2) +
				get(0, 1) * get(1, 2) * get(2, 0) -
				get(2, 0) * get(1, 1) * get(0, 2) -
				get(1, 0) * get(0, 1) * get(2, 2) -
				get(2, 1) * get(1, 2) * get(0, 0);
	}

	public Mat3f inverse () {
		float det = determinant();
		if (det == 0)
			throw new RuntimeException("Matrix is not invertible");

		float[] cofactorArray = new float[SIZE * SIZE];
		for (int y = 0; y < SIZE; y++) {
			for (int x = 0; x < SIZE; x++) {
				cofactorArray[y * SIZE + x] = (float)Math.pow(-1, x + y) * minor(x, y);
			}
		}

		Mat3f adjoint = new Mat3f(cofactorArray).transpose();
		return adjoint.mul(1f / det);
	}

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

	public Mat3f mul (float multiplier) {
		float[] newMatrix = new float[SIZE * SIZE];
		for (int i = 0; i < newMatrix.length; i++) {
			newMatrix[i] = _matrix[i] * multiplier;
		}

		_matrix = newMatrix;
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
