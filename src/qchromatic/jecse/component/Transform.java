package qchromatic.jecse.component;

import qchromatic.jecse.common.Mat4;
import qchromatic.jecse.common.Vec3;
import qchromatic.jecse.core.Component;

public class Transform extends Component {
	private Vec3 _position;
	private Vec3 _rotation;
	private Vec3 _scale;

	private Mat4 _modelMatrix;
	private boolean _dirty;

	// region ctors
	public Transform (Transform other) {
		if (other == null) return;

		_position = new Vec3(other._position);
		_rotation = new Vec3(other._rotation);
		_scale = new Vec3(other._scale);

		_modelMatrix = new Mat4().identity();
		_dirty = true;
	}

	public Transform () {
		_position = new Vec3();
		_rotation = new Vec3();
		_scale = new Vec3(1f);

		_modelMatrix = new Mat4().identity();
		_dirty = true;
	}
	// endregion

	public Vec3 position () { return new Vec3(_position); }
    public Transform position (float x, float y, float z) { return position(new Vec3(x, y, z)); }
	public Transform position (Vec3 position) {
		_position = new Vec3(position);
		_dirty = true;
		return this;
	}

	public Vec3 rotation () { return new Vec3(_rotation); }
    public Transform rotation (float x, float y, float z) { return rotation(new Vec3(x, y, z)); }
	public Transform rotation (Vec3 rotation) {
		_rotation = new Vec3(rotation);
		_dirty = true;
		return this;
	}

	public Vec3 scale () { return new Vec3(_scale); }
    public Transform scale (float x, float y, float z) { return scale(new Vec3(x, y, z)); }
	public Transform scale (Vec3 scale) {
		_scale = new Vec3(scale);
		_dirty = true;
		return this;
	}

	public Mat4 getModelMatrix () {
		if (_dirty) {
			updateModelMatrix();
			_dirty = false;
		}

		return _modelMatrix;
	}

	private void updateModelMatrix () {
		_modelMatrix.identity()
				.mul(Mat4.translation(_position))
				.mul(Mat4.rotation(_rotation))
				.mul(Mat4.scale(_scale));
	}
}
