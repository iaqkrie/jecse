package common.physics;

import qchromatic.jecse.common.Quaternion;
import qchromatic.jecse.component.BoxCollider;
import qchromatic.jecse.component.Rigidbody;
import qchromatic.jecse.component.Transform;
import qchromatic.jecse.core.Entity;
import qchromatic.jecse.engine.Scene;
import qchromatic.jecse.system.PhysicsSystem;

public final class PhysicsSystemTest {
	public static void main (String[] args) {
		testRestingBox();
		testSleepingBodyWakesWhenSupportIsRemoved();
		testScaledColliderUsesLocalHalfExtents();
		testOffCenterContactCreatesRotation();
		testUnsupportedLedgeDoesNotSleep();
		testSpinningBoxSettlesOnFloor();
		testSpinningBoxSettlesInCorner();
	}

	private static void testRestingBox () {
		Scene scene = new Scene();
		scene.addSystem(new PhysicsSystem()
				.gravity(0f, -10f, 0f)
				.fixedTimeStep(1f / 60f)
				.positionIterations(6));

		Entity ground = new Entity("ground")
				.addComponent(new Transform()
						.position(0f, -0.1f, 0f))
				.addComponent(new BoxCollider()
						.halfExtents(5f, 0.1f, 5f)
						.friction(1f));
		scene.addEntity(ground);

		Rigidbody body = new Rigidbody()
				.mass(1f)
				.linearDamping(0f);
		Transform transform = new Transform()
				.position(0f, 2f, 0f);
		Entity box = new Entity("box")
				.addComponent(transform)
				.addComponent(body)
				.addComponent(new BoxCollider()
						.halfExtents(0.5f, 0.5f, 0.5f)
						.friction(1f));
		scene.addEntity(box);

		scene.init();
		for (int i = 0; i < 240; i++)
			scene.loop(1f / 60f);

		expectNear("resting box y", transform.position().y, 0.5f, 0.03f);
		expectNear("resting body velocity y", body.velocity().y, 0f, 0.05f);
		if (!body.sleeping())
			throw new AssertionError("resting body did not go to sleep");

		scene.dispose();
	}

	private static void testSleepingBodyWakesWhenSupportIsRemoved () {
		Scene scene = new Scene();
		scene.addSystem(new PhysicsSystem()
				.gravity(0f, -10f, 0f)
				.fixedTimeStep(1f / 60f)
				.positionIterations(6));

		scene.addEntity(new Entity("ground")
				.addComponent(new Transform()
						.position(0f, -0.1f, 0f))
				.addComponent(new BoxCollider()
						.halfExtents(5f, 0.1f, 5f)
						.friction(1f)));

		Rigidbody body = new Rigidbody()
				.mass(1f);
		Transform transform = new Transform()
				.position(0f, 2f, 0f);
		scene.addEntity(new Entity("box")
				.addComponent(transform)
				.addComponent(body)
				.addComponent(new BoxCollider()
						.halfExtents(0.5f, 0.5f, 0.5f)
						.friction(1f)));

		scene.init();
		for (int i = 0; i < 240; i++)
			scene.loop(1f / 60f);

		if (!body.sleeping())
			throw new AssertionError("body did not sleep before support removal");

		float supportedY = transform.position().y;
		scene.removeEntity("ground");
		for (int i = 0; i < 30; i++)
			scene.loop(1f / 60f);

		if (body.sleeping())
			throw new AssertionError("body stayed asleep after support removal");
		if (transform.position().y >= supportedY - 0.05f)
			throw new AssertionError("body did not fall after support removal");

		scene.dispose();
	}

	private static void testScaledColliderUsesLocalHalfExtents () {
		Scene scene = new Scene();
		scene.addSystem(new PhysicsSystem()
				.gravity(0f, -10f, 0f)
				.fixedTimeStep(1f / 60f)
				.velocityIterations(8)
				.positionIterations(8));

		scene.addEntity(new Entity("scaled-ground")
				.addComponent(new Transform()
						.position(0f, -0.1f, 0f)
						.scale(8f, 0.2f, 8f))
				.addComponent(new BoxCollider()
						.halfExtents(0.5f, 0.5f, 0.5f)
						.friction(1f)));

		Rigidbody body = new Rigidbody()
				.mass(1f);
		Transform transform = new Transform()
				.position(0f, 2f, 0f);
		scene.addEntity(new Entity("box")
				.addComponent(transform)
				.addComponent(body)
				.addComponent(new BoxCollider()
						.halfExtents(0.5f, 0.5f, 0.5f)
						.friction(1f)));

		scene.init();
		for (int i = 0; i < 240; i++)
			scene.loop(1f / 60f);

		expectNear("box on scaled collider y", transform.position().y, 0.5f, 0.03f);
		scene.dispose();
	}

	private static void testOffCenterContactCreatesRotation () {
		Scene scene = new Scene();
		scene.addSystem(new PhysicsSystem()
				.gravity(0f, -10f, 0f)
				.fixedTimeStep(1f / 60f)
				.velocityIterations(8)
				.positionIterations(8));

		Entity support = new Entity("support")
				.addComponent(new Transform()
						.position(0f, 0f, 0f))
				.addComponent(new BoxCollider()
						.halfExtents(0.5f, 0.25f, 2f)
						.friction(0.7f));
		scene.addEntity(support);

		Rigidbody body = new Rigidbody()
				.mass(1f)
				.angularDamping(0.01f);
		Transform transform = new Transform()
				.position(0.75f, 1.25f, 0f);
		Entity box = new Entity("off-center-box")
				.addComponent(transform)
				.addComponent(body)
				.addComponent(new BoxCollider()
						.halfExtents(0.5f, 0.5f, 0.5f)
						.friction(0.7f));
		scene.addEntity(box);

		scene.init();
		for (int i = 0; i < 120; i++)
			scene.loop(1f / 60f);

		Quaternion rotation = transform.rotation();
		float rotationMagnitude = Math.abs(rotation.x) + Math.abs(rotation.y) + Math.abs(rotation.z);
		if (rotationMagnitude < 0.05f)
			throw new AssertionError("off-center contact did not rotate body");

		scene.dispose();
	}

	private static void testUnsupportedLedgeDoesNotSleep () {
		Scene scene = new Scene();
		scene.addSystem(new PhysicsSystem()
				.gravity(0f, -10f, 0f)
				.fixedTimeStep(1f / 60f)
				.velocityIterations(10)
				.positionIterations(10)
				.sleepThresholds(0.06f, 0.08f, 0.4f));

		scene.addEntity(new Entity("narrow-support")
				.addComponent(new Transform()
						.position(0f, 0.25f, 0f))
				.addComponent(new BoxCollider()
						.halfExtents(0.5f, 0.25f, 2f)
						.friction(0.8f)));

		Rigidbody body = new Rigidbody()
				.mass(1f)
				.angularDamping(0.1f);
		Transform transform = new Transform()
				.position(0.82f, 1.6f, 0f);
		scene.addEntity(new Entity("unsupported-box")
				.addComponent(transform)
				.addComponent(body)
				.addComponent(new BoxCollider()
						.halfExtents(0.5f, 0.5f, 0.5f)
						.friction(0.8f)));

		scene.init();
		for (int i = 0; i < 360; i++)
			scene.loop(1f / 60f);

		if (body.sleeping())
			throw new AssertionError("unsupported ledge body went to sleep");
		if (transform.position().y > -0.5f)
			throw new AssertionError("unsupported ledge body did not fall off");

		scene.dispose();
	}

	private static void testSpinningBoxSettlesOnFloor () {
		Scene scene = new Scene();
		scene.addSystem(new PhysicsSystem()
				.gravity(0f, -10f, 0f)
				.fixedTimeStep(1f / 60f)
				.velocityIterations(8)
				.positionIterations(8)
				.rollingFriction(0.25f)
				.contactDamping(0.1f, 0.4f)
				.maxSpeeds(20f, 8f)
				.sleepThresholds(0.08f, 0.12f, 0.35f));

		scene.addEntity(new Entity("ground")
				.addComponent(new Transform()
						.position(0f, -0.1f, 0f)
						.scale(8f, 0.2f, 8f))
				.addComponent(new BoxCollider()
						.halfExtents(0.5f, 0.5f, 0.5f)
						.friction(1f)));

		Rigidbody body = new Rigidbody()
				.mass(1f)
				.angularVelocity(0f, 0f, 10f)
				.linearDamping(0.05f)
				.angularDamping(0.1f);
		Transform transform = new Transform()
				.position(0f, 0.55f, 0f);
		scene.addEntity(new Entity("spinning-box")
				.addComponent(transform)
				.addComponent(body)
				.addComponent(new BoxCollider()
						.halfExtents(0.5f, 0.5f, 0.5f)
						.friction(1f)));

		scene.init();
		for (int i = 0; i < 420; i++)
			scene.loop(1f / 60f);

		if (body.angularVelocity().length() > 0.25f)
			throw new AssertionError("spinning box did not lose angular velocity");
		if (Math.abs(transform.position().x) > 2f || Math.abs(transform.position().z) > 2f)
			throw new AssertionError("spinning box drifted too far");

		scene.dispose();
	}

	private static void testSpinningBoxSettlesInCorner () {
		Scene scene = new Scene();
		scene.addSystem(new PhysicsSystem()
				.gravity(0f, -10f, 0f)
				.fixedTimeStep(1f / 60f)
				.velocityIterations(8)
				.positionIterations(8)
				.rollingFriction(0.35f)
				.contactDamping(0.12f, 0.65f)
				.penetrationFriction(0.35f)
				.maxSpeeds(16f, 6f)
				.sleepThresholds(0.1f, 0.16f, 0.35f));

		scene.addEntity(new Entity("floor")
				.addComponent(new Transform()
						.position(0f, -0.1f, 0f)
						.scale(8f, 0.2f, 8f))
				.addComponent(new BoxCollider()
						.halfExtents(0.5f, 0.5f, 0.5f)
						.friction(1f)));
		scene.addEntity(new Entity("right-wall")
				.addComponent(new Transform()
						.position(4f, 1f, 0f)
						.scale(0.2f, 2f, 8f))
				.addComponent(new BoxCollider()
						.halfExtents(0.5f, 0.5f, 0.5f)
						.friction(1f)));
		scene.addEntity(new Entity("back-wall")
				.addComponent(new Transform()
						.position(0f, 1f, -4f)
						.scale(8f, 2f, 0.2f))
				.addComponent(new BoxCollider()
						.halfExtents(0.5f, 0.5f, 0.5f)
						.friction(1f)));

		Rigidbody body = new Rigidbody()
				.mass(1f)
				.angularVelocity(0f, 8f, 8f)
				.linearDamping(0.1f)
				.angularDamping(0.2f);
		Transform transform = new Transform()
				.position(3.35f, 0.55f, -3.35f);
		scene.addEntity(new Entity("corner-box")
				.addComponent(transform)
				.addComponent(body)
				.addComponent(new BoxCollider()
						.halfExtents(0.5f, 0.5f, 0.5f)
						.friction(1f)));

		scene.init();
		for (int i = 0; i < 480; i++)
			scene.loop(1f / 60f);

		if (body.angularVelocity().length() > 0.3f)
			throw new AssertionError("corner spinning box did not lose angular velocity");
		if (transform.position().x < -4f || transform.position().x > 4f || transform.position().z < -4f || transform.position().z > 4f)
			throw new AssertionError("corner spinning box escaped the platform");

		scene.dispose();
	}

	private static void expectNear (String label, float actual, float expected, float tolerance) {
		if (Math.abs(actual - expected) > tolerance)
			throw new AssertionError(label + ": expected " + expected + " +/- " + tolerance + ", got " + actual);
	}
}
