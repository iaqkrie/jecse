package qchromatic.jecse.component;

import qchromatic.jecse.common.Vec3;
import qchromatic.jecse.core.Component;

public abstract class Collider extends Component {
	private boolean _enabled;
	private boolean _trigger;
	private Vec3 _offset;
	private int _layer;
	private int _mask;
	private float _restitution;
	private float _friction;

	protected Collider () {
		_enabled = true;
		_trigger = false;
		_offset = new Vec3();
		_layer = 1;
		_mask = -1;
		_restitution = 0f;
		_friction = 0.5f;
	}

	public boolean enabled () { return _enabled; }
	public Collider enabled (boolean enabled) {
		_enabled = enabled;
		return this;
	}

	public boolean trigger () { return _trigger; }
	public Collider trigger (boolean trigger) {
		_trigger = trigger;
		return this;
	}

	public Vec3 offset () { return new Vec3(_offset); }
	public Collider offset (Vec3 offset) {
		if (offset == null)
			throw new IllegalArgumentException("Collider offset cannot be null");

		_offset = new Vec3(offset);
		return this;
	}
	public Collider offset (float x, float y, float z) {
		return offset(new Vec3(x, y, z));
	}

	public int layer () { return _layer; }
	public Collider layer (int layer) {
		_layer = layer;
		return this;
	}

	public int mask () { return _mask; }
	public Collider mask (int mask) {
		_mask = mask;
		return this;
	}

	public float restitution () { return _restitution; }
	public Collider restitution (float restitution) {
		_restitution = Math.max(0f, restitution);
		return this;
	}

	public float friction () { return _friction; }
	public Collider friction (float friction) {
		_friction = Math.max(0f, friction);
		return this;
	}
}
