package qchromatic.jecse.component;

import qchromatic.jecse.core.Component;
import qchromatic.jecse.engine.Material;
import qchromatic.jecse.engine.Mesh;

public class MeshRenderer extends Component {
	private Mesh _mesh;
	private Material _material;

	private int _vaoId = -1;

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

	public int getVao () { return _vaoId; }
	public void setVao (int vaoId) { _vaoId = vaoId; }
}
