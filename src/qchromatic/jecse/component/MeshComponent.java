package qchromatic.jecse.component;

import qchromatic.jecse.core.Component;
import qchromatic.jecse.engine.Mesh;
import qchromatic.jecse.graphics.Shader;

public class MeshComponent extends Component {
	private Mesh _mesh;
	private Shader _shader;

	public Mesh mesh () { return _mesh; }
	public MeshComponent mesh (Mesh mesh) {
		_mesh = mesh;
		return this;
	}

	public Shader shader () { return _shader; }
	public MeshComponent shader (Shader shader) {
		_shader = shader;
		return this;
	}
}
