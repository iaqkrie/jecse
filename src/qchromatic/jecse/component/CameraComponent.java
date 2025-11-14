package qchromatic.jecse.component;

import qchromatic.jecse.common.Mat4;
import qchromatic.jecse.core.Component;

public class CameraComponent extends Component {
	private boolean _ortho;

	private float _fov;
	private float _orthoSize;

	private float _aspectRatio;
	private float _near;
	private float _far;

	private Mat4 _viewMatrix;
	private Mat4 _projectionMatrix;

	public CameraComponent () {
		_ortho = false;

		_fov = 60f;
		_orthoSize = 5f;

		_aspectRatio = 16f / 9f;
		_near = 0.1f;
		_far = 100f;

		_projectionMatrix = new Mat4();
		_viewMatrix = new Mat4();
	}

	public boolean ortho () { return _ortho; }
	public CameraComponent ortho (boolean ortho) {
		_ortho = ortho;
		return this;
	}

	public float fov () { return _fov; }
	public CameraComponent fov (float fov) {
		_fov = fov;
		return this;
	}

	public float orthoSize () { return _orthoSize; }
	public CameraComponent orthoSize (float orthoSize) {
		_orthoSize = orthoSize;
		return this;
	}

	public float aspectRatio () { return _aspectRatio; }
	public CameraComponent aspectRatio (float aspectRatio) {
		_aspectRatio = aspectRatio;
		return this;
	}

	public float near () { return _near; }
	public CameraComponent near (float near) {
		_near = near;
		return this;
	}

	public float far () { return _far; }
	public CameraComponent far (float far) {
		_far = far;
		return this;
	}

	public Mat4 getViewMatrix () {
		updateViewMatrix();

		return _viewMatrix;
	}

	public Mat4 getProjectionMatrix () {
		updateProjectionMatrix();

		return _projectionMatrix;
	}

	private void updateViewMatrix () {
		TransformComponent transform = entity.getComponent(TransformComponent.class);
		Mat4 inverseRotation = Mat4.rotation(transform.rotation()).transpose();
		Mat4 inverseTranslation = Mat4.translation(transform.position().multiplied(-1f));

		_viewMatrix.identity()
				.mul(inverseRotation)
				.mul(inverseTranslation);
	}

	private void updateProjectionMatrix () {
		if (_ortho)
			_projectionMatrix = Mat4.ortho(_orthoSize, _aspectRatio, _near, _far);
		else
			_projectionMatrix = Mat4.frustum(_fov, _aspectRatio, _near, _far);
	}
}
