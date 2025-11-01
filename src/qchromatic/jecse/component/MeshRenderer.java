package qchromatic.jecse.component;

import qchromatic.jecse.core.Component;
import qchromatic.jecse.engine.Mesh;
import qchromatic.jecse.graphics.ShaderProgram;

public class MeshRenderer extends Component {
	private Mesh _mesh;
	private ShaderProgram _shader;

	public Mesh mesh () { return _mesh; }
	public MeshRenderer mesh (Mesh mesh) {
		_mesh = mesh;
		return this;
	}

	public ShaderProgram shader () { return _shader; }
	public MeshRenderer shader (ShaderProgram shader) {
		_shader = shader;
		return this;
	}
}
