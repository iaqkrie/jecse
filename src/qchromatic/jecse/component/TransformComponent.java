package qchromatic.jecse.component;

import qchromatic.jecse.common.Mat4;
import qchromatic.jecse.common.Quaternion;
import qchromatic.jecse.common.Vec3;
import qchromatic.jecse.core.Component;

public class TransformComponent extends Component {
	private Vec3 _position;
	private Quaternion _rotation;
	private Vec3 _scale;

	private Mat4 _modelMatrix;

	// region ctors
	public TransformComponent (TransformComponent other) {
		if (other == null) return;

		_position = new Vec3(other._position);
		_rotation = new Quaternion(other._rotation);
		_scale = new Vec3(other._scale);

		_modelMatrix = new Mat4().identity();
	}

	public TransformComponent () {
		this(new Vec3(), new Quaternion(), new Vec3(1f));
	}
	public TransformComponent (Vec3 position, Quaternion rotation, Vec3 scale) {
		_position = new Vec3(position);
		_rotation = new Quaternion(rotation);
		_scale = new Vec3(scale);

		_modelMatrix = new Mat4().identity();
	}
	// endregion

	public Vec3 position () { return new Vec3(_position); }
	public TransformComponent position (Vec3 position) {
		_position = new Vec3(position);
		return this;
	}

	public Quaternion rotation () { return new Quaternion(_rotation); }
	public TransformComponent rotation (Quaternion rotation) {
		_rotation = new Quaternion(rotation);
		return this;
	}

	public Vec3 scale () { return new Vec3(_scale); }
	public TransformComponent scale (Vec3 scale) {
		_scale = new Vec3(scale);
		return this;
	}

	public Mat4 getModelMatrix () {
		return _modelMatrix;
	}

	private void updateModelMatrix () {
		_modelMatrix.identity();

		_modelMatrix
				.mul(Mat4.translation(_position))
				.mul(Mat4.rotation(_rotation))
				.mul(Mat4.scale(_scale));
	}
}
