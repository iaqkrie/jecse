package qchromatic.jecse.component;

import qchromatic.jecse.core.Component;
import qchromatic.jecse.engine.Material;
import qchromatic.jecse.engine.Mesh;
import qchromatic.jecse.graphics.Shader;

public class MeshComponent extends Component {
	private Mesh _mesh;
	private Material _material;

	public Mesh mesh () { return _mesh; }
	public MeshComponent mesh (Mesh mesh) {
		_mesh = mesh;
		return this;
	}

	public Material material () { return _material; }
	public MeshComponent material (Material material) {
		_material = material;
		return this;
	}
}
