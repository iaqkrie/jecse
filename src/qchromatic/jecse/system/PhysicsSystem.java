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
	private static final float CONTACT_TOLERANCE = 0.02f;
	private static final int MAX_CONTACT_POINTS = 4;
	private static final float POSITION_SLOP = 0.01f;
	private static final float BAUMGARTE = 0.2f;
	private static final float MAX_PENETRATION_BIAS = 1.5f;

	private Vec3 _gravity;
	private float _fixedTimeStep;
	private int _maxSubSteps;
	private int _positionIterations;
	private int _velocityIterations;
	private float _rollingFriction;
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

		// Kept for source compatibility; contacts are solved by impulse constraints now.
		return this;
	}

	public PhysicsSystem penetrationFriction (float penetrationFriction) {
		if (penetrationFriction < 0f)
			throw new IllegalArgumentException("Penetration friction cannot be negative");

		// Kept for source compatibility; penetration is corrected by Baumgarte bias now.
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

		for (Contact contact : contacts()) {
			if (contact.a.collider.trigger() || contact.b.collider.trigger()) continue;
			applyRollingFriction(contact, contact.a, contact.normal.multiplied(-1f));
			applyRollingFriction(contact, contact.b, contact.normal);
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

		return new Contact(a, b, result.normal, result.penetration, contactPoints(a.shape, b.shape, result.normal, result.penetration));
	}

	private List<Vec3> contactPoints (BoxShape a, BoxShape b, Vec3 normal, float penetration) {
		List<Vec3> points = new ArrayList<>();
		float plane = contactPlane(a, b, normal);

		collectContactVertices(points, a, b, normal, plane, penetration, true);
		collectContactVertices(points, b, a, normal, plane, penetration, false);

		if (points.isEmpty())
			addUniquePoint(points, contactPatchCenter(a, b, normal, plane), CONTACT_TOLERANCE);

		return reduceContactPoints(points, MAX_CONTACT_POINTS);
	}

	private float contactPlane (BoxShape a, BoxShape b, Vec3 normal) {
		float normalA = a.center.dot(normal) + projectionRadius(a, normal);
		float normalB = b.center.dot(normal) - projectionRadius(b, normal);
		return (normalA + normalB) * 0.5f;
	}

	private Vec3 contactPatchCenter (BoxShape a, BoxShape b, Vec3 normal, float plane) {
		Vec3 tangent0 = contactTangent(normal);
		Vec3 tangent1 = normal.crossed(tangent0).normalized();

		float tangent0Center = overlapCenter(a, b, tangent0);
		float tangent1Center = overlapCenter(a, b, tangent1);

		return normal.multiplied(plane)
				.added(tangent0.multiplied(tangent0Center))
				.added(tangent1.multiplied(tangent1Center));
	}

	private void collectContactVertices (List<Vec3> points, BoxShape source, BoxShape target, Vec3 normal, float plane, float penetration, boolean positiveFace) {
		float face = source.center.dot(normal) + (positiveFace ? projectionRadius(source, normal) : -projectionRadius(source, normal));
		float maxFaceDistance = Math.max(CONTACT_TOLERANCE, penetration + CONTACT_TOLERANCE);

		for (Vec3 vertex : vertices(source)) {
			float projection = vertex.dot(normal);
			float faceDistance = positiveFace ? face - projection : projection - face;
			if (faceDistance > maxFaceDistance) continue;
			if (!inside(vertex, target, maxFaceDistance)) continue;

			addUniquePoint(points, projectOntoPlane(vertex, normal, plane), CONTACT_TOLERANCE);
		}
	}

	private boolean inside (Vec3 point, BoxShape shape, float tolerance) {
		Vec3 offset = subtract(point, shape.center);
		for (int i = 0; i < 3; i++) {
			if (Math.abs(offset.dot(shape.axes[i])) > half(shape, i) + tolerance)
				return false;
		}

		return true;
	}

	private Vec3 projectOntoPlane (Vec3 point, Vec3 normal, float plane) {
		return point.added(normal.multiplied(plane - point.dot(normal)));
	}

	private void addUniquePoint (List<Vec3> points, Vec3 point, float tolerance) {
		float toleranceSquared = tolerance * tolerance;
		for (Vec3 existing : points) {
			if (distanceSquared(existing, point) <= toleranceSquared)
				return;
		}

		points.add(point);
	}

	private List<Vec3> reduceContactPoints (List<Vec3> points, int maxPoints) {
		if (points.size() <= maxPoints)
			return List.copyOf(points);

		List<Vec3> reduced = new ArrayList<>();
		Vec3 center = average(points);
		addFarthestPoint(points, reduced, center);

		while (reduced.size() < maxPoints)
			addMostSeparatedPoint(points, reduced);

		return List.copyOf(reduced);
	}

	private Vec3 average (List<Vec3> points) {
		Vec3 result = new Vec3();
		for (Vec3 point : points)
			result.add(point);

		return result.multiplied(1f / points.size());
	}

	private void addFarthestPoint (List<Vec3> candidates, List<Vec3> result, Vec3 from) {
		Vec3 best = null;
		float bestDistance = -1f;
		for (Vec3 candidate : candidates) {
			float distance = distanceSquared(candidate, from);
			if (distance > bestDistance) {
				bestDistance = distance;
				best = candidate;
			}
		}

		if (best != null)
			result.add(best);
	}

	private void addMostSeparatedPoint (List<Vec3> candidates, List<Vec3> result) {
		Vec3 best = null;
		float bestDistance = -1f;
		for (Vec3 candidate : candidates) {
			if (containsPoint(result, candidate)) continue;

			float nearestDistance = Float.POSITIVE_INFINITY;
			for (Vec3 existing : result)
				nearestDistance = Math.min(nearestDistance, distanceSquared(candidate, existing));

			if (nearestDistance > bestDistance) {
				bestDistance = nearestDistance;
				best = candidate;
			}
		}

		if (best != null)
			result.add(best);
	}

	private boolean containsPoint (List<Vec3> points, Vec3 point) {
		for (Vec3 existing : points) {
			if (distanceSquared(existing, point) <= EPSILON)
				return true;
		}

		return false;
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

		for (Vec3 point : contact.points)
			resolveVelocityAtPoint(contact, point);
	}

	private void resolveVelocityAtPoint (Contact contact, Vec3 point) {
		float invMassA = inverseMass(contact.a.body);
		float invMassB = inverseMass(contact.b.body);
		Vec3 centerA = worldCenterOfMass(contact.a);
		Vec3 centerB = worldCenterOfMass(contact.b);
		Vec3 rA = subtract(point, centerA);
		Vec3 rB = subtract(point, centerB);

		Vec3 relativeVelocity = subtract(pointVelocity(contact.b, rB), pointVelocity(contact.a, rA));
		float velocityAlongNormal = relativeVelocity.dot(contact.normal);
		float velocityBias = contactVelocityBias(contact, velocityAlongNormal);
		if (velocityAlongNormal >= velocityBias)
			return;

		float denominator = invMassA + invMassB
				+ angularDenominator(contact.a, rA, contact.normal)
				+ angularDenominator(contact.b, rB, contact.normal);
		if (denominator <= 0f)
			return;

		float impulseMagnitude = (velocityBias - velocityAlongNormal) / denominator;
		applyImpulse(contact.a, contact.normal.multiplied(-impulseMagnitude), point);
		applyImpulse(contact.b, contact.normal.multiplied(impulseMagnitude), point);

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

				applyImpulse(contact.a, tangent.multiplied(-tangentImpulseMagnitude), point);
				applyImpulse(contact.b, tangent.multiplied(tangentImpulseMagnitude), point);
			}
		}
	}

	private void applyRollingFriction (Contact contact, ColliderEntry entry, Vec3 contactForceDirection) {
		Rigidbody body = entry.body;
		if (_rollingFriction <= 0f || body == null || !body.enabled() || !body.dynamic() || body.sleeping() || body.lockRotation()) return;
		if (!isSupported(contact, entry, contactForceDirection)) return;

		Vec3 axis = contactForceDirection.normalized();
		Vec3 angularVelocity = body.angularVelocity();
		Vec3 spin = axis.multiplied(angularVelocity.dot(axis));
		if (spin.lengthSquared() <= EPSILON) return;

		body.angularVelocity(angularVelocity.added(spin.multiplied(-Math.min(1f, _rollingFriction * _fixedTimeStep))));
	}

	private float contactVelocityBias (Contact contact, float velocityAlongNormal) {
		float bias = 0f;
		float penetration = Math.max(0f, contact.penetration - POSITION_SLOP);
		if (penetration > 0f)
			bias = Math.min(MAX_PENETRATION_BIAS, BAUMGARTE * penetration / Math.max(_fixedTimeStep, EPSILON));

		float restitution = Math.max(contact.a.collider.restitution(), contact.b.collider.restitution());
		if (restitution > 0f && velocityAlongNormal < -0.5f)
			bias += restitution * -velocityAlongNormal;

		return bias;
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

		float percent = 0.2f;
		float correction = Math.min(0.05f, Math.max(0f, contact.penetration - POSITION_SLOP) * percent);
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

		return isSupportDirection(contactForceDirection)
				&& restingNormalImpulse(body, contactForceDirection) > 0f
				&& centerOfMassInsideContactPatch(contact, entry);
	}

	private boolean isSupportDirection (Vec3 contactForceDirection) {
		if (_gravity.lengthSquared() <= EPSILON || contactForceDirection.lengthSquared() <= EPSILON)
			return false;

		Vec3 gravityUp = _gravity.normalized().multiplied(-1f);
		Vec3 supportDirection = contactForceDirection.normalized();
		return supportDirection.dot(gravityUp) >= 0.65f;
	}

	private boolean centerOfMassInsideContactPatch (Contact contact, ColliderEntry entry) {
		if (contact.points.size() < 3)
			return false;

		Vec3 tangent0 = contactTangent(contact.normal);
		Vec3 tangent1 = contact.normal.crossed(tangent0).normalized();
		Vec3 centerOfMass = worldCenterOfMass(entry);

		return insideProjectedContactPatch(centerOfMass, tangent0, contact.points)
				&& insideProjectedContactPatch(centerOfMass, tangent1, contact.points);
	}

	private boolean insideProjectedContactPatch (Vec3 point, Vec3 axis, List<Vec3> contactPoints) {
		float min = Float.POSITIVE_INFINITY;
		float max = Float.NEGATIVE_INFINITY;
		for (Vec3 contactPoint : contactPoints) {
			float projection = contactPoint.dot(axis);
			min = Math.min(min, projection);
			max = Math.max(max, projection);
		}

		if (max - min < 0.05f)
			return false;

		float pointProjection = point.dot(axis);
		float margin = 0.03f;

		return pointProjection >= min - margin && pointProjection <= max + margin;
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

	private float distanceSquared (Vec3 a, Vec3 b) {
		float x = a.x - b.x;
		float y = a.y - b.y;
		float z = a.z - b.z;
		return x * x + y * y + z * z;
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
		private final List<Vec3> points;

		private Contact (ColliderEntry a, ColliderEntry b, Vec3 normal, float penetration, List<Vec3> points) {
			this.a = a;
			this.b = b;
			this.normal = normal;
			this.penetration = penetration;
			this.points = List.copyOf(points);
		}
	}
}
