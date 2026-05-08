package qchromatic.jecse.component;

import qchromatic.jecse.common.Vec3;
import qchromatic.jecse.core.Component;

public class Rigidbody extends Component {
	private PhysicsBodyType _type;
	private Vec3 _velocity;
	private Vec3 _angularVelocity;
	private Vec3 _force;
	private Vec3 _torque;
	private Vec3 _centerOfMass;
	private float _mass;
	private float _gravityScale;
	private float _linearDamping;
	private float _angularDamping;
	private boolean _lockRotation;
	private boolean _useGravity;
	private boolean _enabled;
	private boolean _sleeping;

	public Rigidbody () {
		_type = PhysicsBodyType.DYNAMIC;
		_velocity = new Vec3();
		_angularVelocity = new Vec3();
		_force = new Vec3();
		_torque = new Vec3();
		_centerOfMass = new Vec3();
		_mass = 1f;
		_gravityScale = 1f;
		_linearDamping = 0f;
		_angularDamping = 0f;
		_lockRotation = false;
		_useGravity = true;
		_enabled = true;
		_sleeping = false;
	}

	public PhysicsBodyType type () { return _type; }
	public Rigidbody type (PhysicsBodyType type) {
		if (type == null)
			throw new IllegalArgumentException("Physics body type cannot be null");

		_type = type;
		return this;
	}

	public boolean dynamic () { return _type == PhysicsBodyType.DYNAMIC; }

	public boolean kinematic () { return _type == PhysicsBodyType.KINEMATIC; }

	public boolean staticBody () { return _type == PhysicsBodyType.STATIC; }

	public Vec3 velocity () { return new Vec3(_velocity); }
	public Rigidbody velocity (Vec3 velocity) {
		if (velocity == null)
			throw new IllegalArgumentException("Velocity cannot be null");

		_velocity = new Vec3(velocity);
		if (_velocity.lengthSquared() > 0f)
			wakeUp();
		return this;
	}
	public Rigidbody velocity (float x, float y, float z) {
		return velocity(new Vec3(x, y, z));
	}

	public Vec3 angularVelocity () { return new Vec3(_angularVelocity); }
	public Rigidbody angularVelocity (Vec3 angularVelocity) {
		if (angularVelocity == null)
			throw new IllegalArgumentException("Angular velocity cannot be null");

		_angularVelocity = new Vec3(angularVelocity);
		if (_angularVelocity.lengthSquared() > 0f)
			wakeUp();
		return this;
	}
	public Rigidbody angularVelocity (float x, float y, float z) {
		return angularVelocity(new Vec3(x, y, z));
	}

	public Rigidbody addForce (Vec3 force) {
		if (force != null) {
			_force.add(force);
			if (force.lengthSquared() > 0f)
				wakeUp();
		}

		return this;
	}
	public Rigidbody addForce (float x, float y, float z) {
		return addForce(new Vec3(x, y, z));
	}

	public Rigidbody addForceAtPoint (Vec3 force, Vec3 worldPoint) {
		if (force == null) return this;

		addForce(force);
		if (worldPoint != null && entity != null) {
			Transform transform = entity.getComponent(Transform.class);
			if (transform != null)
				addTorque(worldPoint.added(transform.getModelMatrix().transformPoint(_centerOfMass).multiplied(-1f)).crossed(force));
		}

		return this;
	}
	public Rigidbody addForceAtPoint (float forceX, float forceY, float forceZ, float pointX, float pointY, float pointZ) {
		return addForceAtPoint(new Vec3(forceX, forceY, forceZ), new Vec3(pointX, pointY, pointZ));
	}

	public Rigidbody addTorque (Vec3 torque) {
		if (torque != null) {
			_torque.add(torque);
			if (torque.lengthSquared() > 0f)
				wakeUp();
		}

		return this;
	}
	public Rigidbody addTorque (float x, float y, float z) {
		return addTorque(new Vec3(x, y, z));
	}

	public Rigidbody addImpulse (Vec3 impulse) {
		if (impulse != null && dynamic()) {
			if (impulse.lengthSquared() > 0f)
				wakeUp();
			_velocity.add(impulse.multiplied(inverseMass()));
		}

		return this;
	}
	public Rigidbody addImpulse (float x, float y, float z) {
		return addImpulse(new Vec3(x, y, z));
	}

	public Rigidbody addAngularImpulse (Vec3 angularImpulse) {
		if (angularImpulse != null && dynamic()) {
			if (angularImpulse.lengthSquared() > 0f)
				wakeUp();
			_angularVelocity.add(angularImpulse);
		}

		return this;
	}
	public Rigidbody addAngularImpulse (float x, float y, float z) {
		return addAngularImpulse(new Vec3(x, y, z));
	}

	public Vec3 force () { return new Vec3(_force); }

	public Vec3 torque () { return new Vec3(_torque); }

	public void clearForces () {
		_force = new Vec3();
		_torque = new Vec3();
	}

	public float mass () { return _mass; }
	public Rigidbody mass (float mass) {
		if (mass <= 0f)
			throw new IllegalArgumentException("Mass must be positive");

		_mass = mass;
		return this;
	}

	public Vec3 centerOfMass () { return new Vec3(_centerOfMass); }
	public Rigidbody centerOfMass (Vec3 centerOfMass) {
		if (centerOfMass == null)
			throw new IllegalArgumentException("Center of mass cannot be null");

		_centerOfMass = new Vec3(centerOfMass);
		return this;
	}
	public Rigidbody centerOfMass (float x, float y, float z) {
		return centerOfMass(new Vec3(x, y, z));
	}

	public float inverseMass () {
		return dynamic() ? 1f / _mass : 0f;
	}

	public float gravityScale () { return _gravityScale; }
	public Rigidbody gravityScale (float gravityScale) {
		_gravityScale = gravityScale;
		return this;
	}

	public float linearDamping () { return _linearDamping; }
	public Rigidbody linearDamping (float linearDamping) {
		_linearDamping = Math.max(0f, linearDamping);
		return this;
	}

	public float angularDamping () { return _angularDamping; }
	public Rigidbody angularDamping (float angularDamping) {
		_angularDamping = Math.max(0f, angularDamping);
		return this;
	}

	public boolean lockRotation () { return _lockRotation; }
	public Rigidbody lockRotation (boolean lockRotation) {
		_lockRotation = lockRotation;
		return this;
	}

	public boolean useGravity () { return _useGravity; }
	public Rigidbody useGravity (boolean useGravity) {
		_useGravity = useGravity;
		return this;
	}

	public boolean enabled () { return _enabled; }
	public Rigidbody enabled (boolean enabled) {
		_enabled = enabled;
		return this;
	}

	public boolean sleeping () { return _sleeping; }
	public Rigidbody sleep () {
		_sleeping = true;
		_velocity = new Vec3();
		_angularVelocity = new Vec3();
		clearForces();
		return this;
	}

	public Rigidbody wakeUp () {
		_sleeping = false;
		return this;
	}
}
