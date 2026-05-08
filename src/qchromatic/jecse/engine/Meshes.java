package qchromatic.jecse.engine;

public final class Meshes {
	public static Mesh triangle () {
		float[] vertices = {
				-0.5f, -0.5f, 0f, 0f, 0f, 0f, 0f, 1f,
				 0.5f, -0.5f, 0f, 1f, 0f, 0f, 0f, 1f,
				 0f,    0.5f, 0f, 0.5f, 1f, 0f, 0f, 1f
		};

		int[] triangles = {
				0, 1, 2
		};

		return new Mesh(vertices, triangles, 8);
	}

	public static Mesh quad () {
		float[] vertices = {
				-0.5f,  0.5f, 0f, 0f, 0f, 0f, 0f, 1f,
				-0.5f, -0.5f, 0f, 0f, 1f, 0f, 0f, 1f,
				 0.5f,  0.5f, 0f, 1f, 0f, 0f, 0f, 1f,
				 0.5f, -0.5f, 0f, 1f, 1f, 0f, 0f, 1f
		};

		int[] triangles = {
				0, 1, 3,
				0, 3, 2
		};

		return new Mesh(vertices, triangles, 8);
	}

	public static Mesh cube () {
		float[] vertices = {
				-0.5f, -0.5f,  0.5f, 0f, 1f,  0f,  0f,  1f,
				 0.5f, -0.5f,  0.5f, 1f, 1f,  0f,  0f,  1f,
				 0.5f,  0.5f,  0.5f, 1f, 0f,  0f,  0f,  1f,
				-0.5f,  0.5f,  0.5f, 0f, 0f,  0f,  0f,  1f,

				 0.5f, -0.5f, -0.5f, 0f, 1f,  0f,  0f, -1f,
				-0.5f, -0.5f, -0.5f, 1f, 1f,  0f,  0f, -1f,
				-0.5f,  0.5f, -0.5f, 1f, 0f,  0f,  0f, -1f,
				 0.5f,  0.5f, -0.5f, 0f, 0f,  0f,  0f, -1f,

				-0.5f, -0.5f, -0.5f, 0f, 1f, -1f,  0f,  0f,
				-0.5f, -0.5f,  0.5f, 1f, 1f, -1f,  0f,  0f,
				-0.5f,  0.5f,  0.5f, 1f, 0f, -1f,  0f,  0f,
				-0.5f,  0.5f, -0.5f, 0f, 0f, -1f,  0f,  0f,

				 0.5f, -0.5f,  0.5f, 0f, 1f,  1f,  0f,  0f,
				 0.5f, -0.5f, -0.5f, 1f, 1f,  1f,  0f,  0f,
				 0.5f,  0.5f, -0.5f, 1f, 0f,  1f,  0f,  0f,
				 0.5f,  0.5f,  0.5f, 0f, 0f,  1f,  0f,  0f,

				-0.5f,  0.5f,  0.5f, 0f, 1f,  0f,  1f,  0f,
				 0.5f,  0.5f,  0.5f, 1f, 1f,  0f,  1f,  0f,
				 0.5f,  0.5f, -0.5f, 1f, 0f,  0f,  1f,  0f,
				-0.5f,  0.5f, -0.5f, 0f, 0f,  0f,  1f,  0f,

				-0.5f, -0.5f, -0.5f, 0f, 1f,  0f, -1f,  0f,
				 0.5f, -0.5f, -0.5f, 1f, 1f,  0f, -1f,  0f,
				 0.5f, -0.5f,  0.5f, 1f, 0f,  0f, -1f,  0f,
				-0.5f, -0.5f,  0.5f, 0f, 0f,  0f, -1f,  0f
		};

		int[] triangles = {
				 0,  1,  2,  0,  2,  3,
				 4,  5,  6,  4,  6,  7,
				 8,  9, 10,  8, 10, 11,
				12, 13, 14, 12, 14, 15,
				16, 17, 18, 16, 18, 19,
				20, 21, 22, 20, 22, 23
		};

		return new Mesh(vertices, triangles, 8);
	}
}
