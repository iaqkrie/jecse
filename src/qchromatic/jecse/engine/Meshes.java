package qchromatic.jecse.engine;

public final class Meshes {
	public static Mesh triangle () {
		float[] vertices = {
				-1f, -1f, 0f,
				 1f, -1f, 0f,
				 0f,  1f, 0f
		};

		int[] triangles = {
				0, 1, 2
		};

		return new Mesh(vertices, triangles);
	}

	public static Mesh quad () {
		float[] vertices = {
				-1f, -1f, 0f,
				 1f, -1f, 0f,
				 1f,  1f, 0f,
				-1f,  1f, 0f
		};

		int[] triangles = {
				0, 1, 3,
				1, 2, 3
		};

		return new Mesh(vertices, triangles);
	}
}
