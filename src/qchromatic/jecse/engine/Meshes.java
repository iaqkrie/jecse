package qchromatic.jecse.engine;

public final class Meshes {
	public static Mesh triangle () {
		float[] vertices = {
				-0.5f, -0.5f, 0f,
				 0.5f, -0.5f, 0f,
				 0f,    0.5f, 0f
		};

		int[] triangles = {
				0, 1, 2
		};

		return new Mesh(vertices, triangles);
	}

	public static Mesh quad () {
		float[] vertices = {
				-0.5f,  0.5f, 0f,		0f, 0f,
				-0.5f, -0.5f, 0f,		0f, 1f,
				 0.5f,  0.5f, 0f,		1f, 0f,
				 0.5f, -0.5f, 0f,		1f, 1f
		};

		int[] triangles = {
				0, 1, 3,
				0, 3, 2
		};

		return new Mesh(vertices, triangles);
	}

	public static Mesh cube () {
		float[] vertices = {
				// 0								// 1
				-0.5f,  0.5f, -0.5f,	0f, 0f,		0.5f,  0.5f, -0.5f,		1f, 0f,
				// 2								// 3
				-0.5f,  0.5f,  0.5f,	0f, 1f,		0.5f,  0.5f,  0.5f,		1f, 1f,
				// 4								// 5
				-0.5f, -0.5f, -0.5f,	0f, 1f,		0.5f, -0.5f, -0.5f,		1f, 1f,
				// 6								// 7
				-0.5f, -0.5f,  0.5f,	0f, 0f,		0.5f, -0.5f,  0.5f,		1f, 0f
		};

		int[] triangles = {
				0, 2, 3,	0, 3, 1,
				0, 4, 6,	0, 6, 2,
				2, 6, 7,	2, 7, 3,
				3, 7, 5,	3, 5, 1,
				1, 5, 4,	1, 4, 0,
				5, 7, 6,	5, 6, 4
		};

		return new Mesh(vertices, triangles);
	}
}
