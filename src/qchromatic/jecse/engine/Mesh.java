package qchromatic.jecse.engine;

public class Mesh {
	private float[] _vertices;
	private int[] _triangles;

	public Mesh (float[] vertices, int[] triangles) {
		_vertices = vertices;
		_triangles = triangles;
	}
}
