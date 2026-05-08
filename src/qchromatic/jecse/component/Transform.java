package qchromatic.jecse.component;

import qchromatic.jecse.common.Mat4;
import qchromatic.jecse.common.Quaternion;
import qchromatic.jecse.common.Vec3;
import qchromatic.jecse.core.Component;

import java.util.ArrayList;
import java.util.List;

public class Transform extends Component {
	private Vec3 _position;
	private Quaternion _rotation;
	private Vec3 _scale;

	private Transform _parent;
	private final List<Transform> _children;

	private Mat4 _localMatrix;
	private Mat4 _modelMatrix;
	private boolean _dirty;

	public Transform (Transform other) {
		this();
		if (other == null) return;

		_position = new Vec3(other._position);
		_rotation = new Quaternion(other._rotation);
		_scale = new Vec3(other._scale);
		markDirty();
	}

	public Transform () {
		_position = new Vec3();
		_rotation = new Quaternion();
		_scale = new Vec3(1f);
		_parent = null;
		_children = new ArrayList<>();

		_localMatrix = new Mat4().identity();
		_modelMatrix = new Mat4().identity();
		_dirty = true;
	}

	@Override
	public void onDetach () {
		parent(null);
		for (Transform child : List.copyOf(_children))
			child.parent(null);
	}

	public Vec3 position () { return new Vec3(_position); }
    public Transform position (float x, float y, float z) { return position(new Vec3(x, y, z)); }
	public Transform position (Vec3 position) {
		if (position == null)
			throw new IllegalArgumentException("Position cannot be null");

		_position = new Vec3(position);
		markDirty();
		return this;
	}

	public Quaternion rotation () { return new Quaternion(_rotation); }
    public Transform rotation (float x, float y, float z) { return rotation(Quaternion.euler(x, y, z)); }
	public Transform rotation (Quaternion rotation) {
		if (rotation == null)
			throw new IllegalArgumentException("Rotation cannot be null");

		_rotation = new Quaternion(rotation);
		markDirty();
		return this;
	}

	public Vec3 scale () { return new Vec3(_scale); }
    public Transform scale (float x, float y, float z) { return scale(new Vec3(x, y, z)); }
	public Transform scale (Vec3 scale) {
		if (scale == null)
			throw new IllegalArgumentException("Scale cannot be null");

		_scale = new Vec3(scale);
		markDirty();
		return this;
	}

	public Transform parent () { return _parent; }
	public Transform parent (Transform parent) {
		if (parent == this)
			throw new IllegalArgumentException("Transform cannot be parent of itself");
		if (parent != null && parent.hasParent(this))
			throw new IllegalArgumentException("Transform hierarchy cycle");
		if (_parent == parent) return this;

		if (_parent != null)
			_parent._children.remove(this);

		_parent = parent;

		if (_parent != null && !_parent._children.contains(this))
			_parent._children.add(this);

		markDirty();
		return this;
	}

	public List<Transform> children () {
		return List.copyOf(_children);
	}

	public Transform addChild (Transform child) {
		if (child != null)
			child.parent(this);

		return this;
	}

	public Transform removeChild (Transform child) {
		if (child != null && child._parent == this)
			child.parent(null);

		return this;
	}

	public Vec3 worldPosition () {
		if (_parent == null)
			return position();

		return _parent.getModelMatrix().transformPoint(_position);
	}

	public Quaternion worldRotation () {
		if (_parent == null)
			return rotation();

		return _parent.worldRotation().mul(new Quaternion(_rotation));
	}

	public Mat4 getLocalMatrix () {
		if (_dirty)
			updateMatrices();

		return _localMatrix;
	}

	public Mat4 getModelMatrix () {
		if (_dirty)
			updateMatrices();

		return _modelMatrix;
	}

	private void updateMatrices () {
		_localMatrix.identity()
				.mul(Mat4.translation(_position))
				.mul(_rotation.toMat4())
				.mul(Mat4.scale(_scale));

		if (_parent == null)
			_modelMatrix = new Mat4(_localMatrix.getMatrix());
		else
			_modelMatrix = new Mat4(_parent.getModelMatrix().getMatrix()).mul(_localMatrix);

		_dirty = false;
	}

	private void markDirty () {
		_dirty = true;

		for (Transform child : List.copyOf(_children))
			child.markDirty();
	}

	private boolean hasParent (Transform parent) {
		Transform current = _parent;
		while (current != null) {
			if (current == parent)
				return true;

			current = current._parent;
		}

		return false;
	}
}
