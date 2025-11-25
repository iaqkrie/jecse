package qchromatic.jecse.component;

import qchromatic.jecse.common.Mat4;
import qchromatic.jecse.common.Vec3;
import qchromatic.jecse.core.Component;

public class Transform extends Component {
	private Vec3 _position;
	private Vec3 _rotation;
	private Vec3 _scale;

	private Mat4 _modelMatrix;

	// region ctors
	public Transform (Transform other) {
		if (other == null) return;

		_position = new Vec3(other._position);
		_rotation = new Vec3(other._rotation);
		_scale = new Vec3(other._scale);

		_modelMatrix = new Mat4().identity();
	}

	public Transform () {
		_position = new Vec3();
		_rotation = new Vec3();
		_scale = new Vec3(1f);

		_modelMatrix = new Mat4().identity();
	}
	// endregion

	public Vec3 position () { return new Vec3(_position); }
    public Transform position (float x, float y, float z) {
        _position = new Vec3(x, y, z);
        return this;
    }
	public Transform position (Vec3 position) {
		_position = new Vec3(position);
		return this;
	}

	public Vec3 rotation () { return new Vec3(_rotation); }
    public Transform rotation (float x, float y, float z) {
        _rotation = new Vec3(x, y, z);
        return this;
    }
	public Transform rotation (Vec3 rotation) {
		_rotation = new Vec3(rotation);
		return this;
	}

	public Vec3 scale () { return new Vec3(_scale); }
    public Transform scale (float x, float y, float z) {
        _scale = new Vec3(x, y, z);
        return this;
    }
	public Transform scale (Vec3 scale) {
		_scale = new Vec3(scale);
		return this;
	}

	public Mat4 getModelMatrix () {
		updateModelMatrix();

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
