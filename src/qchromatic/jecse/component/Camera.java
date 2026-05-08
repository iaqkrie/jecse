package qchromatic.jecse.component;

import qchromatic.jecse.common.Mat4;
import qchromatic.jecse.common.Quaternion;
import qchromatic.jecse.common.Vec3;
import qchromatic.jecse.core.Component;

public class Camera extends Component {
	private boolean _ortho;

	private float _fov;
	private float _orthoSize;

	private float _aspectRatio;
	private float _near;
	private float _far;
	private int _priority;
	private int _cullingMask;
	private boolean _enabled;

	private Mat4 _viewMatrix;
	private Mat4 _projectionMatrix;

	public Camera () {
		_ortho = false;

		_fov = 60f;
		_orthoSize = 5f;

		_aspectRatio = 16f / 9f;
		_near = 1f;
		_far = 100f;
		_priority = 0;
		_cullingMask = -1;
		_enabled = true;

		_projectionMatrix = new Mat4();
		_viewMatrix = new Mat4();
	}

	public boolean ortho () { return _ortho; }
	public Camera ortho (boolean ortho) {
		_ortho = ortho;
		return this;
	}

	public float fov () { return _fov; }
	public Camera fov (float fov) {
		if (fov <= 0f || fov >= 180f)
			throw new IllegalArgumentException("Camera fov must be between 0 and 180 degrees");

		_fov = fov;
		return this;
	}

	public float orthoSize () { return _orthoSize; }
	public Camera orthoSize (float orthoSize) {
		if (orthoSize <= 0f)
			throw new IllegalArgumentException("Camera ortho size must be positive");

		_orthoSize = orthoSize;
		return this;
	}

	public float aspectRatio () { return _aspectRatio; }
	public Camera aspectRatio (float aspectRatio) {
		if (aspectRatio <= 0f)
			throw new IllegalArgumentException("Camera aspect ratio must be positive");

		_aspectRatio = aspectRatio;
		return this;
	}

	public float near () { return _near; }
	public Camera near (float near) {
		if (near <= 0f || near >= _far)
			throw new IllegalArgumentException("Camera near plane must be positive and less than far plane");

		_near = near;
		return this;
	}

	public float far () { return _far; }
	public Camera far (float far) {
		if (far <= _near)
			throw new IllegalArgumentException("Camera far plane must be greater than near plane");

		_far = far;
		return this;
	}

	public int priority () { return _priority; }
	public Camera priority (int priority) {
		_priority = priority;
		return this;
	}

	public int cullingMask () { return _cullingMask; }
	public Camera cullingMask (int cullingMask) {
		_cullingMask = cullingMask;
		return this;
	}

	public boolean enabled () { return _enabled; }
	public Camera enabled (boolean enabled) {
		_enabled = enabled;
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
		Transform transform = entity.getComponent(Transform.class);
		if (transform == null) return;

		Quaternion invRotation = transform.worldRotation().conjugated();
		Vec3 invPosition = transform.worldPosition().multiplied(-1f);

		_viewMatrix = invRotation.toMat4()
				.mul(Mat4.translation(invPosition));
	}

	private void updateProjectionMatrix () {
		if (_ortho)
			_projectionMatrix = Mat4.ortho(_orthoSize, _aspectRatio, _near, _far);
		else
			_projectionMatrix = Mat4.frustum(_fov, _aspectRatio, _near, _far);
	}
}
