package qchromatic.jecse.system;

import qchromatic.jecse.common.Mat4;
import qchromatic.jecse.common.Quaternion;
import qchromatic.jecse.common.Vec3;
import qchromatic.jecse.common.Vec4;
import qchromatic.jecse.component.BoxCollider;
import qchromatic.jecse.component.Collider;
import qchromatic.jecse.component.Rigidbody;
import qchromatic.jecse.component.Transform;
import qchromatic.jecse.core.Entity;
import qchromatic.jecse.core.EntityQuery;
import qchromatic.jecse.core.System;
import qchromatic.jecse.engine.DebugRenderer;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class PhysicsSystem extends System {
	private static final float EPSILON = 0.00001f;

	private Vec3 _gravity;
	private float _fixedTimeStep;
	private int _maxSubSteps;
	private int _positionIterations;
	private int _velocityIterations;
	private float _rollingFriction;
	private float _contactLinearDamping;
	private float _contactAngularDamping;
	private float _penetrationFriction;
	private float _maxLinearSpeed;
	private float _maxAngularSpeed;
	private float _sleepLinearThreshold;
	private float _sleepAngularThreshold;
	private float _sleepDelay;
	private boolean _debugDrawColliders;
	private float _accumulator;

	private final Map<Rigidbody, Float> _sleepTimers;
	private EntityQuery _bodies;
	private EntityQuery _colliders;

	public PhysicsSystem () {
		super(500);
		_gravity = new Vec3(0f, -9.81f, 0f);
		_fixedTimeStep = 1f / 60f;
		_maxSubSteps = 5;
		_positionIterations = 6;
		_velocityIterations = 8;
		_rollingFriction = 0.12f;
		_contactLinearDamping = 0.04f;
		_contactAngularDamping = 0.18f;
		_penetrationFriction = 0.2f;
		_maxLinearSpeed = 30f;
		_maxAngularSpeed = 12f;
		_sleepLinearThreshold = 0.05f;
		_sleepAngularThreshold = 0.05f;
		_sleepDelay = 0.5f;
		_debugDrawColliders = false;
		_accumulator = 0f;
		_sleepTimers = new IdentityHashMap<>();
	}

	public Vec3 gravity () { return new Vec3(_gravity); }
	public PhysicsSystem gravity (Vec3 gravity) {
		if (gravity == null)
			throw new IllegalArgumentException("Gravity cannot be null");

		_gravity = new Vec3(gravity);
		return this;
	}
	public PhysicsSystem gravity (float x, float y, float z) {
		return gravity(new Vec3(x, y, z));
	}

	public float fixedTimeStep () { return _fixedTimeStep; }
	public PhysicsSystem fixedTimeStep (float fixedTimeStep) {
		if (fixedTimeStep <= 0f)
			throw new IllegalArgumentException("Physics fixed time step must be positive");

		_fixedTimeStep = fixedTimeStep;
		return this;
	}

	public int maxSubSteps () { return _maxSubSteps; }
	public PhysicsSystem maxSubSteps (int maxSubSteps) {
		if (maxSubSteps <= 0)
			throw new IllegalArgumentException("Physics max sub steps must be positive");

		_maxSubSteps = maxSubSteps;
		return this;
	}

	public int positionIterations () { return _positionIterations; }
	public PhysicsSystem positionIterations (int positionIterations) {
		if (positionIterations <= 0)
			throw new IllegalArgumentException("Physics position iterations must be positive");

		_positionIterations = positionIterations;
		return this;
	}

	public int velocityIterations () { return _velocityIterations; }
	public PhysicsSystem velocityIterations (int velocityIterations) {
		if (velocityIterations <= 0)
			throw new IllegalArgumentException("Physics velocity iterations must be positive");

		_velocityIterations = velocityIterations;
		return this;
	}

	public float rollingFriction () { return _rollingFriction; }
	public PhysicsSystem rollingFriction (float rollingFriction) {
		_rollingFriction = Math.max(0f, rollingFriction);
		return this;
	}

	public PhysicsSystem contactDamping (float linear, float angular) {
		if (linear < 0f || angular < 0f)
			throw new IllegalArgumentException("Contact damping cannot be negative");

		_contactLinearDamping = linear;
		_contactAngularDamping = angular;
		return this;
	}

	public PhysicsSystem penetrationFriction (float penetrationFriction) {
		_penetrationFriction = Math.max(0f, penetrationFriction);
		return this;
	}

	public PhysicsSystem maxSpeeds (float linear, float angular) {
		if (linear <= 0f || angular <= 0f)
			throw new IllegalArgumentException("Max speeds must be positive");

		_maxLinearSpeed = linear;
		_maxAngularSpeed = angular;
		return this;
	}

	public PhysicsSystem sleepThresholds (float linear, float angular, float delay) {
		if (linear < 0f || angular < 0f || delay < 0f)
			throw new IllegalArgumentException("Sleep thresholds cannot be negative");

		_sleepLinearThreshold = linear;
		_sleepAngularThreshold = angular;
		_sleepDelay = delay;
		return this;
	}

	public boolean debugDrawColliders () { return _debugDrawColliders; }
	public PhysicsSystem debugDrawColliders (boolean debugDrawColliders) {
		_debugDrawColliders = debugDrawColliders;
		return this;
	}

	@Override
	public void init () {
		_bodies = scene.query(Transform.class, Rigidbody.class);
		_colliders = scene.queryInheritance(Transform.class, Collider.class);
	}

	@Override
	public void start () {
		_accumulator = 0f;
		_sleepTimers.clear();
	}

	@Override
	public void loop (float dtime) {
		if (dtime <= 0f) return;

		_accumulator += Math.min(dtime, _fixedTimeStep * _maxSubSteps);

		int steps = 0;
		while (_accumulator >= _fixedTimeStep && steps < _maxSubSteps) {
			step(_fixedTimeStep);
			_accumulator -= _fixedTimeStep;
			steps++;
		}

		if (steps == _maxSubSteps && _accumulator >= _fixedTimeStep)
			_accumulator = 0f;

		if (_debugDrawColliders)
			drawColliderBounds();
	}

	private void step (float dtime) {
		integrateForces(dtime);
		integrateTransforms(dtime);
		solveVelocity();
		solvePosition();
		updateSleeping(dtime);
	}

	private void integrateForces (float dtime) {
		for (Entity entity : _bodies) {
			Transform transform = entity.getComponent(Transform.class);
			Rigidbody body = entity.getComponent(Rigidbody.class);
			if (transform == null || body == null || !body.enabled()) continue;

			if (body.staticBody()) {
				body.velocity(0f, 0f, 0f);
				body.angularVelocity(0f, 0f, 0f);
				body.clearForces();
				continue;
			}

			if (body.sleeping()) {
				body.clearForces();
				continue;
			}

			if (body.dynamic()) {
				Vec3 velocity = body.velocity();
				Vec3 acceleration = body.force().multiplied(body.inverseMass());
				if (body.useGravity())
					acceleration.add(_gravity.multiplied(body.gravityScale()));

				velocity.add(acceleration.multiplied(dtime));
				velocity.mul(Math.max(0f, 1f - body.linearDamping() * dtime));
				body.velocity(velocity);

				Vec3 angularVelocity = body.angularVelocity();
				if (!body.lockRotation()) {
					Vec3 angularAcceleration = applyInverseInertia(entity, transform, body, body.torque());
					angularVelocity.add(angularAcceleration.multiplied(dtime));
					angularVelocity.mul(Math.max(0f, 1f - body.angularDamping() * dtime));
					body.angularVelocity(angularVelocity);
				} else {
					body.angularVelocity(0f, 0f, 0f);
				}
			}

			clampBodySpeeds(body);
			body.clearForces();
		}
	}

	private void integrateTransforms (float dtime) {
		for (Entity entity : _bodies) {
			Transform transform = entity.getComponent(Transform.class);
			Rigidbody body = entity.getComponent(Rigidbody.class);
			if (transform == null || body == null || !body.enabled() || body.staticBody() || body.sleeping()) continue;

			transform.position(transform.position().added(body.velocity().multiplied(dtime)));

			Vec3 angularVelocity = body.angularVelocity();
			if (!body.lockRotation() && angularVelocity.lengthSquared() > 0f) {
				float angleRad = angularVelocity.length() * dtime;
				Vec3 axis = angularVelocity.normalized();
				Quaternion delta = Quaternion.axisAngle(axis, (float) Math.toDegrees(angleRad));
				transform.rotation(delta.multiplied(transform.rotation()).normalized());
			}
		}
	}

	private void solveVelocity () {
		for (int iteration = 0; iteration < _velocityIterations; iteration++) {
			for (Contact contact : contacts()) {
				if (contact.a.collider.trigger() || contact.b.collider.trigger()) continue;
				resolveVelocity(contact);
			}
		}
	}

	private void solvePosition () {
		for (int iteration = 0; iteration < _positionIterations; iteration++) {
			for (Contact contact : contacts()) {
				if (contact.a.collider.trigger() || contact.b.collider.trigger()) continue;
				resolvePosition(contact);
			}
		}
	}

	private List<Contact> contacts () {
		List<ColliderEntry> entries = colliderEntries();
		List<Contact> result = new ArrayList<>();

		for (int i = 0; i < entries.size(); i++) {
			for (int j = i + 1; j < entries.size(); j++) {
				ColliderEntry a = entries.get(i);
				ColliderEntry b = entries.get(j);

				if (!canCollide(a.collider, b.collider)) continue;

				Contact contact = contact(a, b);
				if (contact != null)
					result.add(contact);
			}
		}

		return result;
	}

	private List<ColliderEntry> colliderEntries () {
		List<ColliderEntry> result = new ArrayList<>();

		for (Entity entity : _colliders) {
			Transform transform = entity.getComponent(Transform.class);
			Collider collider = firstCollider(entity);
			if (transform == null || collider == null || !collider.enabled()) continue;

			BoxShape shape = boxShape(transform, collider);
			if (shape == null) continue;

			result.add(new ColliderEntry(entity, transform, collider, entity.getComponent(Rigidbody.class), shape));
		}

		return result;
	}

	private Collider firstCollider (Entity entity) {
		List<Collider> colliders = entity.getComponentsInheritance(Collider.class);
		return colliders.isEmpty() ? null : colliders.get(0);
	}

	private boolean canCollide (Collider a, Collider b) {
		return (a.layer() & b.mask()) != 0 && (b.layer() & a.mask()) != 0;
	}

	private Contact contact (ColliderEntry a, ColliderEntry b) {
		if (!aabbOverlap(a.shape, b.shape)) return null;

		SatResult result = new SatResult();
		Vec3 centerDelta = subtract(b.shape.center, a.shape.center);

		for (int i = 0; i < 3; i++) {
			if (!testAxis(a.shape.axes[i], centerDelta, a.shape, b.shape, result)) return null;
			if (!testAxis(b.shape.axes[i], centerDelta, a.shape, b.shape, result)) return null;
		}

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				Vec3 axis = a.shape.axes[i].crossed(b.shape.axes[j]);
				if (axis.lengthSquared() > EPSILON && !testAxis(axis, centerDelta, a.shape, b.shape, result))
					return null;
			}
		}

		if (result.normal.lengthSquared() <= 0f)
			return null;

		return new Contact(a, b, result.normal, result.penetration, contactPoint(a.shape, b.shape, result.normal));
	}

	private Vec3 contactPoint (BoxShape a, BoxShape b, Vec3 normal) {
		Vec3 tangent0 = contactTangent(normal);
		Vec3 tangent1 = normal.crossed(tangent0).normalized();

		float normalA = a.center.dot(normal) + projectionRadius(a, normal);
		float normalB = b.center.dot(normal) - projectionRadius(b, normal);
		float tangent0Center = overlapCenter(a, b, tangent0);
		float tangent1Center = overlapCenter(a, b, tangent1);

		return normal.multiplied((normalA + normalB) * 0.5f)
				.added(tangent0.multiplied(tangent0Center))
				.added(tangent1.multiplied(tangent1Center));
	}

	private float overlapCenter (BoxShape a, BoxShape b, Vec3 axis) {
		float minA = a.center.dot(axis) - projectionRadius(a, axis);
		float maxA = a.center.dot(axis) + projectionRadius(a, axis);
		float minB = b.center.dot(axis) - projectionRadius(b, axis);
		float maxB = b.center.dot(axis) + projectionRadius(b, axis);

		return (Math.max(minA, minB) + Math.min(maxA, maxB)) * 0.5f;
	}

	private boolean testAxis (Vec3 axis, Vec3 centerDelta, BoxShape a, BoxShape b, SatResult result) {
		Vec3 normal = axis.normalized();
		float radiusA = projectionRadius(a, normal);
		float radiusB = projectionRadius(b, normal);
		float distance = centerDelta.dot(normal);
		float overlap = radiusA + radiusB - Math.abs(distance);

		if (overlap <= 0f)
			return false;

		if (overlap < result.penetration) {
			result.penetration = overlap;
			result.normal = distance >= 0f ? normal : normal.multiplied(-1f);
		}

		return true;
	}

	private float projectionRadius (BoxShape shape, Vec3 axis) {
		return shape.halfExtents.x * Math.abs(shape.axes[0].dot(axis))
				+ shape.halfExtents.y * Math.abs(shape.axes[1].dot(axis))
				+ shape.halfExtents.z * Math.abs(shape.axes[2].dot(axis));
	}

	private Vec3 supportPoint (BoxShape shape, Vec3 direction) {
		Vec3 result = new Vec3(shape.center);
		for (int i = 0; i < 3; i++) {
			float dot = shape.axes[i].dot(direction);
			if (dot > EPSILON)
				result.add(shape.axes[i].multiplied(half(shape, i)));
			else if (dot < -EPSILON)
				result.add(shape.axes[i].multiplied(-half(shape, i)));
		}

		return result;
	}

	private BoxShape boxShape (Transform transform, Collider collider) {
		if (!(collider instanceof BoxCollider box))
			return null;

		Mat4 matrix = transform.getModelMatrix();
		Vec3 localHalf = box.halfExtents();

		Vec3[] axes = new Vec3[3];
		float[] half = new float[3];
		for (int i = 0; i < 3; i++) {
			Vec3 column = new Vec3(matrix.get(0, i), matrix.get(1, i), matrix.get(2, i));
			float length = column.length();
			if (length <= EPSILON) {
				axes[i] = fallbackAxis(i);
				half[i] = 0f;
			} else {
				axes[i] = column.multiplied(1f / length);
				half[i] = half(localHalf, i) * length;
			}
		}

		Vec3 center = matrix.transformPoint(box.offset());
		return new BoxShape(center, axes, new Vec3(half[0], half[1], half[2]));
	}

	private Vec3 fallbackAxis (int index) {
		if (index == 0) return new Vec3(1f, 0f, 0f);
		if (index == 1) return new Vec3(0f, 1f, 0f);
		return new Vec3(0f, 0f, 1f);
	}

	private boolean aabbOverlap (BoxShape a, BoxShape b) {
		return a.min.x <= b.max.x && a.max.x >= b.min.x
				&& a.min.y <= b.max.y && a.max.y >= b.min.y
				&& a.min.z <= b.max.z && a.max.z >= b.min.z;
	}

	private void resolveVelocity (Contact contact) {
		wakeForContact(contact);

		float invMassA = inverseMass(contact.a.body);
		float invMassB = inverseMass(contact.b.body);
		Vec3 centerA = worldCenterOfMass(contact.a);
		Vec3 centerB = worldCenterOfMass(contact.b);
		Vec3 rA = subtract(contact.point, centerA);
		Vec3 rB = subtract(contact.point, centerB);

		Vec3 relativeVelocity = subtract(pointVelocity(contact.b, rB), pointVelocity(contact.a, rA));
		float velocityAlongNormal = relativeVelocity.dot(contact.normal);
		if (velocityAlongNormal > 0f) {
			applyContactDamping(contact, contactFrictionImpulse(contact, 0f));
			return;
		}

		float denominator = invMassA + invMassB
				+ angularDenominator(contact.a, rA, contact.normal)
				+ angularDenominator(contact.b, rB, contact.normal);
		if (denominator <= 0f) {
			applyContactDamping(contact, contactFrictionImpulse(contact, 0f));
			return;
		}

		float restitution = Math.min(contact.a.collider.restitution(), contact.b.collider.restitution());
		if (Math.abs(velocityAlongNormal) < 0.5f)
			restitution = 0f;

		float impulseMagnitude = -(1f + restitution) * velocityAlongNormal / denominator;
		applyImpulse(contact.a, contact.normal.multiplied(-impulseMagnitude), contact.point);
		applyImpulse(contact.b, contact.normal.multiplied(impulseMagnitude), contact.point);

		relativeVelocity = subtract(pointVelocity(contact.b, rB), pointVelocity(contact.a, rA));
		Vec3 tangent = subtract(relativeVelocity, contact.normal.multiplied(relativeVelocity.dot(contact.normal)));
		if (tangent.lengthSquared() > EPSILON) {
			tangent.normalize();
			float tangentDenominator = invMassA + invMassB
					+ angularDenominator(contact.a, rA, tangent)
					+ angularDenominator(contact.b, rB, tangent);
			if (tangentDenominator > 0f) {
				float tangentImpulseMagnitude = -relativeVelocity.dot(tangent) / tangentDenominator;
				float friction = (float) Math.sqrt(contact.a.collider.friction() * contact.b.collider.friction());
				float maxFriction = Math.abs(impulseMagnitude) * friction;
				tangentImpulseMagnitude = clamp(tangentImpulseMagnitude, -maxFriction, maxFriction);

				applyImpulse(contact.a, tangent.multiplied(-tangentImpulseMagnitude), contact.point);
				applyImpulse(contact.b, tangent.multiplied(tangentImpulseMagnitude), contact.point);
			}
		}

		applyContactDamping(contact, contactFrictionImpulse(contact, Math.abs(impulseMagnitude)));
	}

	private float contactFrictionImpulse (Contact contact, float normalImpulse) {
		return Math.max(Math.max(normalImpulse, restingNormalImpulse(contact)), penetrationFrictionImpulse(contact));
	}

	private float restingNormalImpulse (Contact contact) {
		return restingNormalImpulse(contact.a.body, contact.normal.multiplied(-1f))
				+ restingNormalImpulse(contact.b.body, contact.normal);
	}

	private float restingNormalImpulse (Rigidbody body, Vec3 contactForceDirection) {
		if (body == null || !body.enabled() || !body.dynamic() || !body.useGravity())
			return 0f;

		Vec3 gravity = _gravity.multiplied(body.gravityScale());
		float supportAcceleration = -gravity.dot(contactForceDirection);
		if (supportAcceleration <= 0f)
			return 0f;

		return body.mass() * supportAcceleration * _fixedTimeStep;
	}

	private float penetrationFrictionImpulse (Contact contact) {
		float invMassSum = inverseMass(contact.a.body) + inverseMass(contact.b.body);
		if (invMassSum <= 0f)
			return 0f;

		return contact.penetration * _penetrationFriction / Math.max(_fixedTimeStep, EPSILON) / invMassSum;
	}

	private void wakeForContact (Contact contact) {
		wakeForContact(contact, contact.a, contact.b, contact.normal.multiplied(-1f));
		wakeForContact(contact, contact.b, contact.a, contact.normal);
	}

	private void wakeForContact (Contact contact, ColliderEntry entry, ColliderEntry other, Vec3 contactForceDirection) {
		Rigidbody body = entry.body;
		if (body == null || !body.enabled() || !body.dynamic() || !body.sleeping()) return;

		if (!isSupported(contact, entry, contactForceDirection) || !isStableSupport(other.body) || isActiveBody(other.body))
			body.wakeUp();
	}

	private void resolvePosition (Contact contact) {
		float invMassA = inverseMass(contact.a.body);
		float invMassB = inverseMass(contact.b.body);
		float invMassSum = invMassA + invMassB;
		if (invMassSum <= 0f) return;

		float slop = 0.002f;
		float percent = 0.65f;
		float correction = Math.max(0f, contact.penetration - slop) * percent;
		if (correction <= 0f) return;

		if (invMassA > 0f)
			move(contact.a.transform, contact.normal.multiplied(-correction * invMassA / invMassSum));
		if (invMassB > 0f)
			move(contact.b.transform, contact.normal.multiplied(correction * invMassB / invMassSum));
	}

	private Vec3 pointVelocity (ColliderEntry entry, Vec3 radius) {
		return velocity(entry.body).added(angularVelocity(entry.body).crossed(radius));
	}

	private void applyImpulse (ColliderEntry entry, Vec3 impulse, Vec3 point) {
		Rigidbody body = entry.body;
		if (body == null || !body.enabled() || !body.dynamic() || body.sleeping()) return;

		body.velocity(body.velocity().added(impulse.multiplied(body.inverseMass())));

		if (!body.lockRotation()) {
			Vec3 radius = subtract(point, worldCenterOfMass(entry));
			Vec3 angularImpulse = applyInverseInertia(entry, radius.crossed(impulse));
			body.angularVelocity(body.angularVelocity().added(angularImpulse));
		}

		clampBodySpeeds(body);
	}

	private void applyContactDamping (Contact contact, float normalImpulse) {
		float friction = (float) Math.sqrt(contact.a.collider.friction() * contact.b.collider.friction());
		float contactImpulse = Math.abs(normalImpulse) * friction;

		dampTangentVelocity(contact.a.body, contact.normal, contactImpulse * _contactLinearDamping);
		dampTangentVelocity(contact.b.body, contact.normal, contactImpulse * _contactLinearDamping);

		float angularDamping = contactImpulse * (_rollingFriction + _contactAngularDamping);
		dampAngularVelocity(contact.a.body, angularDamping);
		dampAngularVelocity(contact.b.body, angularDamping);
	}

	private void dampTangentVelocity (Rigidbody body, Vec3 normal, float amount) {
		if (body == null || !body.enabled() || !body.dynamic() || body.sleeping()) return;

		Vec3 velocity = body.velocity();
		Vec3 normalVelocity = normal.multiplied(velocity.dot(normal));
		Vec3 tangentVelocity = subtract(velocity, normalVelocity);
		float speed = tangentVelocity.length();
		if (speed <= EPSILON) return;

		float factor = Math.max(0f, 1f - amount / speed);
		body.velocity(normalVelocity.added(tangentVelocity.multiplied(factor)));
		clampBodySpeeds(body);
	}

	private void dampAngularVelocity (Rigidbody body, float amount) {
		if (body == null || !body.enabled() || !body.dynamic() || body.sleeping() || body.lockRotation()) return;

		Vec3 angular = body.angularVelocity();
		float speed = angular.length();
		if (speed <= EPSILON) return;

		body.angularVelocity(angular.multiplied(Math.max(0f, 1f - amount / speed)));
		clampBodySpeeds(body);
	}

	private float angularDenominator (ColliderEntry entry, Vec3 radius, Vec3 direction) {
		Vec3 angular = applyInverseInertia(entry, radius.crossed(direction)).crossed(radius);
		return direction.dot(angular);
	}

	private float inverseMass (Rigidbody body) {
		return body == null || !body.enabled() || body.sleeping() ? 0f : body.inverseMass();
	}

	private Vec3 applyInverseInertia (ColliderEntry entry, Vec3 angularImpulse) {
		return applyInverseInertia(entry.entity, entry.transform, entry.body, angularImpulse);
	}

	private Vec3 applyInverseInertia (Entity entity, Transform transform, Rigidbody body, Vec3 angularImpulse) {
		if (entity == null || transform == null || body == null || !body.enabled() || !body.dynamic() || body.lockRotation() || body.sleeping())
			return new Vec3();

		BoxCollider box = entity.getComponent(BoxCollider.class);
		if (box == null)
			return angularImpulse.multiplied(1f / body.mass());

		BoxShape shape = boxShape(transform, box);
		float width = shape.halfExtents.x * 2f;
		float height = shape.halfExtents.y * 2f;
		float depth = shape.halfExtents.z * 2f;
		float massFactor = body.mass() / 12f;
		float[] inverseInertia = {
				safeInverse(massFactor * (height * height + depth * depth)),
				safeInverse(massFactor * (width * width + depth * depth)),
				safeInverse(massFactor * (width * width + height * height))
		};

		Vec3 result = new Vec3();
		for (int i = 0; i < 3; i++)
			result.add(shape.axes[i].multiplied(angularImpulse.dot(shape.axes[i]) * inverseInertia[i]));

		return result;
	}

	private Vec3 worldCenterOfMass (ColliderEntry entry) {
		if (entry.body == null)
			return entry.shape.center;

		return entry.transform.getModelMatrix().transformPoint(entry.body.centerOfMass());
	}

	private void updateSleeping (float dtime) {
		Map<Rigidbody, Boolean> supported = new IdentityHashMap<>();
		Map<Rigidbody, Boolean> touchedByActiveBody = new IdentityHashMap<>();
		for (Contact contact : contacts()) {
			if (contact.a.collider.trigger() || contact.b.collider.trigger()) continue;
			if (isSupported(contact, contact.a, contact.normal.multiplied(-1f)) && isStableSupport(contact.b.body))
				supported.put(contact.a.body, true);
			if (isSupported(contact, contact.b, contact.normal) && isStableSupport(contact.a.body))
				supported.put(contact.b.body, true);
			if (isActiveBody(contact.b.body) && contact.a.body != null)
				touchedByActiveBody.put(contact.a.body, true);
			if (isActiveBody(contact.a.body) && contact.b.body != null)
				touchedByActiveBody.put(contact.b.body, true);
		}

		for (Entity entity : _bodies) {
			Rigidbody body = entity.getComponent(Rigidbody.class);
			if (body == null || !body.enabled() || !body.dynamic()) continue;

			if (!supported.containsKey(body)) {
				_sleepTimers.remove(body);
				if (body.sleeping())
					body.wakeUp();
				continue;
			}

			if (touchedByActiveBody.containsKey(body)) {
				_sleepTimers.remove(body);
				if (body.sleeping())
					body.wakeUp();
				continue;
			}

			boolean slow = body.velocity().lengthSquared() <= _sleepLinearThreshold * _sleepLinearThreshold
					&& body.angularVelocity().lengthSquared() <= _sleepAngularThreshold * _sleepAngularThreshold;
			if (!slow) {
				_sleepTimers.remove(body);
				continue;
			}

			float sleepTime = _sleepTimers.getOrDefault(body, 0f) + dtime;
			_sleepTimers.put(body, sleepTime);
			if (sleepTime >= _sleepDelay)
				body.sleep();
		}
	}

	private boolean isSupported (Contact contact, ColliderEntry entry, Vec3 contactForceDirection) {
		Rigidbody body = entry.body;
		if (body == null || !body.enabled() || !body.dynamic())
			return false;
		if (_gravity.lengthSquared() <= EPSILON)
			return false;

		Vec3 gravityUp = _gravity.normalized().multiplied(-1f);
		Vec3 supportDirection = contactForceDirection.normalized();
		return supportDirection.dot(gravityUp) >= 0.65f
				&& restingNormalImpulse(body, contactForceDirection) > 0f
				&& centerOfMassInsideContactPatch(contact, entry);
	}

	private boolean centerOfMassInsideContactPatch (Contact contact, ColliderEntry entry) {
		Vec3 tangent0 = contactTangent(contact.normal);
		Vec3 tangent1 = contact.normal.crossed(tangent0).normalized();
		Vec3 centerOfMass = worldCenterOfMass(entry);

		return insideProjectedOverlap(centerOfMass, tangent0, contact.a.shape, contact.b.shape)
				&& insideProjectedOverlap(centerOfMass, tangent1, contact.a.shape, contact.b.shape);
	}

	private boolean insideProjectedOverlap (Vec3 point, Vec3 axis, BoxShape a, BoxShape b) {
		float minA = a.center.dot(axis) - projectionRadius(a, axis);
		float maxA = a.center.dot(axis) + projectionRadius(a, axis);
		float minB = b.center.dot(axis) - projectionRadius(b, axis);
		float maxB = b.center.dot(axis) + projectionRadius(b, axis);
		float overlapMin = Math.max(minA, minB);
		float overlapMax = Math.min(maxA, maxB);
		float pointProjection = point.dot(axis);
		float margin = 0.03f;

		return pointProjection >= overlapMin - margin && pointProjection <= overlapMax + margin;
	}

	private Vec3 contactTangent (Vec3 normal) {
		Vec3 reference = Math.abs(normal.y) < 0.9f ? new Vec3(0f, 1f, 0f) : new Vec3(1f, 0f, 0f);
		Vec3 tangent = normal.crossed(reference);
		if (tangent.lengthSquared() <= EPSILON)
			tangent = normal.crossed(new Vec3(0f, 0f, 1f));

		return tangent.normalized();
	}

	private boolean isStableSupport (Rigidbody body) {
		if (body == null || !body.enabled() || body.staticBody())
			return true;
		if (!body.kinematic())
			return false;

		return body.velocity().lengthSquared() <= _sleepLinearThreshold * _sleepLinearThreshold
				&& body.angularVelocity().lengthSquared() <= _sleepAngularThreshold * _sleepAngularThreshold;
	}

	private boolean isActiveBody (Rigidbody body) {
		if (body == null || !body.enabled())
			return false;

		return body.dynamic() && !body.sleeping()
				|| body.kinematic() && (body.velocity().lengthSquared() > _sleepLinearThreshold * _sleepLinearThreshold
				|| body.angularVelocity().lengthSquared() > _sleepAngularThreshold * _sleepAngularThreshold);
	}

	private Vec3 velocity (Rigidbody body) {
		return body == null ? new Vec3() : body.velocity();
	}

	private Vec3 angularVelocity (Rigidbody body) {
		return body == null ? new Vec3() : body.angularVelocity();
	}

	private void move (Transform transform, Vec3 delta) {
		transform.position(transform.position().added(delta));
	}

	private void clampBodySpeeds (Rigidbody body) {
		if (body == null || !body.enabled() || !body.dynamic()) return;

		body.velocity(clampMagnitude(body.velocity(), _maxLinearSpeed));
		if (!body.lockRotation())
			body.angularVelocity(clampMagnitude(body.angularVelocity(), _maxAngularSpeed));
	}

	private Vec3 clampMagnitude (Vec3 value, float maxLength) {
		float length = value.length();
		if (length <= maxLength || length <= EPSILON)
			return value;

		return value.multiplied(maxLength / length);
	}

	private void drawColliderBounds () {
		for (ColliderEntry entry : colliderEntries()) {
			Vec4 color = entry.collider.trigger()
					? new Vec4(1f, 0.3f, 1f, 1f)
					: entry.body == null || entry.body.staticBody()
							? new Vec4(0.2f, 1f, 0.35f, 1f)
							: entry.body.kinematic()
									? new Vec4(1f, 0.85f, 0.2f, 1f)
									: new Vec4(0.2f, 0.85f, 1f, 1f);

			drawOrientedBox(entry.shape, color);
			if (entry.body != null)
				DebugRenderer.line(worldCenterOfMass(entry), worldCenterOfMass(entry).added(new Vec3(0f, 0.25f, 0f)), new Vec4(1f, 0.1f, 0.1f, 1f));
		}
	}

	private void drawOrientedBox (BoxShape shape, Vec4 color) {
		Vec3[] vertices = vertices(shape);
		int[][] edges = {
				{0, 1}, {0, 2}, {0, 4}, {7, 6}, {7, 5}, {7, 3},
				{1, 3}, {1, 5}, {2, 3}, {2, 6}, {4, 5}, {4, 6}
		};

		for (int[] edge : edges)
			DebugRenderer.line(vertices[edge[0]], vertices[edge[1]], color);
	}

	private Vec3[] vertices (BoxShape shape) {
		Vec3[] result = new Vec3[8];
		int index = 0;
		for (int x = -1; x <= 1; x += 2) {
			for (int y = -1; y <= 1; y += 2) {
				for (int z = -1; z <= 1; z += 2) {
					result[index++] = shape.center
							.added(shape.axes[0].multiplied(shape.halfExtents.x * x))
							.added(shape.axes[1].multiplied(shape.halfExtents.y * y))
							.added(shape.axes[2].multiplied(shape.halfExtents.z * z));
				}
			}
		}

		return result;
	}

	private Vec3 subtract (Vec3 a, Vec3 b) {
		return new Vec3(a.x - b.x, a.y - b.y, a.z - b.z);
	}

	private float half (BoxShape shape, int index) {
		return half(shape.halfExtents, index);
	}

	private float half (Vec3 halfExtents, int index) {
		if (index == 0) return halfExtents.x;
		if (index == 1) return halfExtents.y;
		return halfExtents.z;
	}

	private float safeInverse (float value) {
		return value <= 0f ? 0f : 1f / value;
	}

	private float clamp (float value, float min, float max) {
		return Math.max(min, Math.min(max, value));
	}

	private static final class ColliderEntry {
		private final Entity entity;
		private final Transform transform;
		private final Collider collider;
		private final Rigidbody body;
		private final BoxShape shape;

		private ColliderEntry (Entity entity, Transform transform, Collider collider, Rigidbody body, BoxShape shape) {
			this.entity = entity;
			this.transform = transform;
			this.collider = collider;
			this.body = body;
			this.shape = shape;
		}
	}

	private static final class BoxShape {
		private final Vec3 center;
		private final Vec3[] axes;
		private final Vec3 halfExtents;
		private final Vec3 min;
		private final Vec3 max;

		private BoxShape (Vec3 center, Vec3[] axes, Vec3 halfExtents) {
			this.center = center;
			this.axes = axes;
			this.halfExtents = halfExtents;

			Vec3[] vertices = new Vec3[8];
			int index = 0;
			for (int x = -1; x <= 1; x += 2) {
				for (int y = -1; y <= 1; y += 2) {
					for (int z = -1; z <= 1; z += 2) {
						vertices[index++] = center
								.added(axes[0].multiplied(halfExtents.x * x))
								.added(axes[1].multiplied(halfExtents.y * y))
								.added(axes[2].multiplied(halfExtents.z * z));
					}
				}
			}

			Vec3 min = new Vec3(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY);
			Vec3 max = new Vec3(Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY, Float.NEGATIVE_INFINITY);
			for (Vec3 vertex : vertices) {
				min.x = Math.min(min.x, vertex.x);
				min.y = Math.min(min.y, vertex.y);
				min.z = Math.min(min.z, vertex.z);
				max.x = Math.max(max.x, vertex.x);
				max.y = Math.max(max.y, vertex.y);
				max.z = Math.max(max.z, vertex.z);
			}

			this.min = min;
			this.max = max;
		}
	}

	private static final class SatResult {
		private float penetration;
		private Vec3 normal;

		private SatResult () {
			penetration = Float.POSITIVE_INFINITY;
			normal = new Vec3();
		}
	}

	private static final class Contact {
		private final ColliderEntry a;
		private final ColliderEntry b;
		private final Vec3 normal;
		private final float penetration;
		private final Vec3 point;

		private Contact (ColliderEntry a, ColliderEntry b, Vec3 normal, float penetration, Vec3 point) {
			this.a = a;
			this.b = b;
			this.normal = normal;
			this.penetration = penetration;
			this.point = point;
		}
	}
}
