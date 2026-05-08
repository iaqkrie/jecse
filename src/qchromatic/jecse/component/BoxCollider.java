package qchromatic.jecse.component;

import qchromatic.jecse.common.Vec3;

public class BoxCollider extends Collider {
	private Vec3 _halfExtents;

	public BoxCollider () {
		_halfExtents = new Vec3(0.5f);
	}

	public Vec3 halfExtents () { return new Vec3(_halfExtents); }
	public BoxCollider halfExtents (Vec3 halfExtents) {
		if (halfExtents == null)
			throw new IllegalArgumentException("Box collider half extents cannot be null");
		if (halfExtents.x <= 0f || halfExtents.y <= 0f || halfExtents.z <= 0f)
			throw new IllegalArgumentException("Box collider half extents must be positive");

		_halfExtents = new Vec3(halfExtents);
		return this;
	}
	public BoxCollider halfExtents (float x, float y, float z) {
		return halfExtents(new Vec3(x, y, z));
	}

	public Vec3 size () {
		return _halfExtents.multiplied(2f);
	}
	public BoxCollider size (Vec3 size) {
		if (size == null)
			throw new IllegalArgumentException("Box collider size cannot be null");

		return halfExtents(size.multiplied(0.5f));
	}
	public BoxCollider size (float x, float y, float z) {
		return size(new Vec3(x, y, z));
	}
}
