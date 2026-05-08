package qchromatic.jecse.component;

import qchromatic.jecse.core.Component;
import qchromatic.jecse.engine.Material;
import qchromatic.jecse.engine.Mesh;

public class MeshRenderer extends Component {
	private Mesh _mesh;
	private Material _material;
	private boolean _enabled;
	private int _layer;
	private int _renderQueue;
	private float _boundsRadius;

	public MeshRenderer () {
		_enabled = true;
		_layer = 1;
		_renderQueue = 0;
		_boundsRadius = 1f;
	}

	public Mesh mesh () { return _mesh; }
	public MeshRenderer mesh (Mesh mesh) {
		_mesh = mesh;
		return this;
	}

	public Material material () { return _material; }
	public MeshRenderer material (Material material) {
		_material = material;
		return this;
	}

	public boolean enabled () { return _enabled; }
	public MeshRenderer enabled (boolean enabled) {
		_enabled = enabled;
		return this;
	}

	public int layer () { return _layer; }
	public MeshRenderer layer (int layer) {
		_layer = layer;
		return this;
	}

	public int renderQueue () { return _renderQueue; }
	public MeshRenderer renderQueue (int renderQueue) {
		_renderQueue = renderQueue;
		return this;
	}

	public float boundsRadius () { return _boundsRadius; }
	public MeshRenderer boundsRadius (float boundsRadius) {
		_boundsRadius = Math.max(0f, boundsRadius);
		return this;
	}
}
