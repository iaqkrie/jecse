package qchromatic.jecse.component;

import qchromatic.jecse.common.Mat4;
import qchromatic.jecse.core.Component;

public class CameraComponent extends Component {
	private boolean _ortho;

	// perspective projection settings
	private float _fov;
	private float _aspectRatio;
	private float _near;
	private float _far;

	// orthographic projection settings
	private float _orthoSize;

	private Mat4 _projectionMatrix;
	private Mat4 _viewMatrix;

	public CameraComponent () {
		_ortho = false;

		_fov = 60f;
		_aspectRatio = 16f / 9f;
		_near = 0.1f;
		_far = 100f;

		_orthoSize = 5f;

		_projectionMatrix = new Mat4();
		_viewMatrix = new Mat4();
	}

	// TODO
}
