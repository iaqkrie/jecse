package qchromatic.jecse.engine;

public class Mesh {
	private float[] _vertices;
	private int[] _triangles;
	private int _vertexStride;
	private long _version;

	public Mesh (float[] vertices, int[] triangles) {
		this(vertices, triangles, inferVertexStride(vertices));
	}

	public Mesh (float[] vertices, int[] triangles, int vertexStride) {
		if (vertices == null)
			throw new IllegalArgumentException("Vertices cannot be null");
		if (triangles == null)
			throw new IllegalArgumentException("Triangles cannot be null");
		if (vertexStride < 3)
			throw new IllegalArgumentException("Vertex stride must be at least 3");
		if (vertices.length % vertexStride != 0)
			throw new IllegalArgumentException("Vertices length must be divisible by vertex stride");

		_vertices = vertices.clone();
		_triangles = triangles.clone();
		_vertexStride = vertexStride;
		_version = 1;
	}

	public float[] vertices () { return _vertices.clone(); }
	public Mesh vertices (float[] vertices) {
		if (vertices == null)
			throw new IllegalArgumentException("Vertices cannot be null");
		if (vertices.length % _vertexStride != 0)
			throw new IllegalArgumentException("Vertices length must be divisible by vertex stride");

		_vertices = vertices.clone();
		_version++;
		return this;
	}

	public int[] triangles () { return _triangles.clone(); }
	public Mesh triangles (int[] triangles) {
		if (triangles == null)
			throw new IllegalArgumentException("Triangles cannot be null");

		_triangles = triangles.clone();
		_version++;
		return this;
	}

	public int vertexStride () { return _vertexStride; }

	public Mesh vertexStride (int vertexStride) {
		if (vertexStride < 3)
			throw new IllegalArgumentException("Vertex stride must be at least 3");
		if (_vertices.length % vertexStride != 0)
			throw new IllegalArgumentException("Vertices length must be divisible by vertex stride");

		_vertexStride = vertexStride;
		_version++;
		return this;
	}

	public int vertexCount () { return _vertices.length / _vertexStride; }

	public int indexCount () { return _triangles.length; }

	public long version () { return _version; }

	private static int inferVertexStride (float[] vertices) {
		if (vertices == null) return 3;
		if (vertices.length % 5 == 0) return 5;
		return 3;
	}
}
