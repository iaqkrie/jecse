package qchromatic.jecse.engine;

public class Mesh {
	private float[] _vertices;
	private int[] _triangles;

	public Mesh (float[] vertices, int[] triangles) {
		_vertices = vertices.clone();
		_triangles = triangles.clone();
	}

	public float[] vertices () { return _vertices; }
	public Mesh vertices (float[] vertices) {
		_vertices = vertices.clone();
		return this;
	}

	public int[] triangles () { return _triangles; }
	public Mesh triangles (int[] triangles) {
		_triangles = triangles.clone();
		return this;
	}

	public static Mesh createTriangleMesh () {
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
}
