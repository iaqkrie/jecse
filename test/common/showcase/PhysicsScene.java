package common.showcase;

import qchromatic.jecse.common.Quaternion;
import qchromatic.jecse.common.Vec3;
import qchromatic.jecse.common.Vec4;
import qchromatic.jecse.component.BoxCollider;
import qchromatic.jecse.component.Camera;
import qchromatic.jecse.component.DirectionalLight;
import qchromatic.jecse.component.MeshRenderer;
import qchromatic.jecse.component.PhysicsBodyType;
import qchromatic.jecse.component.Rigidbody;
import qchromatic.jecse.component.Script;
import qchromatic.jecse.component.Transform;
import qchromatic.jecse.core.Entity;
import qchromatic.jecse.engine.Application;
import qchromatic.jecse.engine.DebugRenderer;
import qchromatic.jecse.engine.EngineContext;
import qchromatic.jecse.engine.EngineConfig;
import qchromatic.jecse.engine.Input;
import qchromatic.jecse.engine.KeyCode;
import qchromatic.jecse.engine.Material;
import qchromatic.jecse.engine.Meshes;
import qchromatic.jecse.engine.Scene;
import qchromatic.jecse.script.FreecamController;
import qchromatic.jecse.system.DebugRenderSystem;
import qchromatic.jecse.system.PhysicsSystem;
import qchromatic.jecse.system.RenderSystem;
import qchromatic.jecse.system.ScriptSystem;

public final class PhysicsScene {
	private static final int LAYER_WORLD = 1;
	private static final int LAYER_PHYSICS = 1 << 1;

	public static void main (String[] args) {
		EngineConfig config = new EngineConfig()
				.clearColor(0.05f, 0.07f, 0.08f, 1f)
				.autoUpdateCameraAspect(true)
				.debugOpenGLErrors(false);

		Application app = new Application("jecse physics", config);
		app.getWindow().setCursorDisabled();
		app.context().sceneManager().load(createScene());
		app.run();
	}

	private static Scene createScene () {
		Scene scene = new Scene();

		// Скрипты идут первыми: здесь они могут добавлять силы, torque или импульсы в Rigidbody.
		scene.addSystem(new ScriptSystem());

		// Физика идет после скриптов и до рендера, поэтому RenderSystem получает уже обновленные Transform.
		scene.addSystem(new PhysicsSystem()
				.gravity(0f, -9.81f, 0f)
				.fixedTimeStep(1f / 60f)
				.maxSubSteps(5)
				.velocityIterations(12)
				.positionIterations(10)
				.maxSpeeds(16f, 8f)
				.sleepThresholds(0.08f, 0.12f, 0.5f)
				.debugDrawColliders(true));

		scene.addSystem(new RenderSystem()
				.ambientColor(new Vec4(0.18f, 0.20f, 0.23f, 1f)));

		// DebugRenderSystem рисует bounds коллайдеров и маркеры center of mass.
		scene.addSystem(new DebugRenderSystem());

		scene.addEntity(new Entity("sun")
				.addComponent(new DirectionalLight()
						.direction(-0.4f, -1f, -0.25f)
						.color(new Vec4(1f, 0.94f, 0.82f, 1f))
						.intensity(1.2f)));

		scene.addEntity(new Entity("camera")
				.addComponent(new Transform()
						.position(0f, 4f, 9f)
						.rotation(Quaternion.euler(-22f, 0f, 0f)))
				.addComponent(new Camera()
						.fov(60f)
						.near(0.1f)
						.far(80f)
						.cullingMask(LAYER_WORLD | LAYER_PHYSICS)
						.priority(10))
				.addComponent(new FreecamController()
						.speed(7f)
						.sensitivity(18f)));

		addFloor(scene);
		addWall(scene, "left-wall", -4f, 1f, 0f, 0.25f, 1.2f, 4f);
		addWall(scene, "right-wall", 4f, 1f, 0f, 0.25f, 1.2f, 4f);
		addWall(scene, "back-wall", 0f, 1f, -4f, 4f, 1.2f, 0.25f);

		// Узкая опора и смещенный куб проверяют устойчивость: центр масс вне contact patch должен опрокинуть тело.
		addNarrowSupport(scene);
		addDynamicBox(scene, "off-center-box", 0.75f, 3.2f, -1.2f, new Vec4(0.25f, 0.68f, 1f, 1f), new Vec3(0f, 0f, 0f));

		addDynamicBox(scene, "falling-box-a", -1.2f, 4.5f, 0f, new Vec4(0.25f, 0.68f, 1f, 1f), new Vec3(1.6f, 0f, 0f));
		addDynamicBox(scene, "falling-box-b", 0.1f, 7f, 0.2f, new Vec4(1f, 0.55f, 0.24f, 1f), new Vec3(-0.6f, 0f, 0.2f));
		addDynamicBox(scene, "falling-box-c", 3f, 7f, 0.2f, new Vec4(1f, 0f, 1f, 1f), new Vec3(-0.6f, 0f, 0.2f));
		addDynamicBox(scene, "low-bounce-box", 1.4f, 5.8f, -0.5f, new Vec4(0.55f, 1f, 0.32f, 1f), new Vec3(-1.1f, 0f, 0.5f), 0.08f);

		// Kinematic body не получает гравитацию, но двигается своей velocity и сталкивается с dynamic телами.
		scene.addEntity(new Entity("kinematic-platform")
				.addComponent(new Transform()
						.position(0f, 0.35f, 3.1f)
						.scale(1.8f, 0.25f, 0.7f))
				.addComponent(new Rigidbody()
						.type(PhysicsBodyType.KINEMATIC)
						.velocity(0.9f, 0f, 0f)
						.useGravity(false))
				.addComponent(new BoxCollider()
						.halfExtents(0.5f, 0.5f, 0.5f)
						.friction(0.9f))
				.addComponent(new MeshRenderer()
						.mesh(Meshes.cube())
						.material(new Material().color(new Vec4(1f, 0.9f, 0.28f, 1f)))
						.layer(LAYER_PHYSICS)
						.boundsRadius(2f))
				.addComponent(new PlatformBounceScript(-2.4f, 2.4f)));

		scene.addEntity(new Entity("physics-grid")
				.addComponent(new PhysicsGridScript()));

		scene.addEntity(new Entity("scene-controls")
				.addComponent(new PhysicsSceneControlScript()));

		return scene;
	}

	private static void addFloor (Scene scene) {
		scene.addEntity(new Entity("floor")
				.addComponent(new Transform()
						.position(0f, -0.1f, 0f)
						.scale(8f, 0.2f, 8f))
				.addComponent(new BoxCollider()
						.halfExtents(0.5f, 0.5f, 0.5f)
						.friction(1f)
						.restitution(0f))
				.addComponent(new MeshRenderer()
						.mesh(Meshes.cube())
						.material(new Material().color(new Vec4(0.18f, 0.24f, 0.22f, 1f)))
						.layer(LAYER_WORLD)
						.boundsRadius(6f)));
	}

	private static void addNarrowSupport (Scene scene) {
		scene.addEntity(new Entity("narrow-support")
				.addComponent(new Transform()
						.position(0f, 0.45f, -1.2f)
						.scale(1f, 0.5f, 2.2f))
				.addComponent(new BoxCollider()
						.halfExtents(0.5f, 0.5f, 0.5f)
						.friction(0.9f))
				.addComponent(new MeshRenderer()
						.mesh(Meshes.cube())
						.material(new Material().color(new Vec4(0.33f, 0.28f, 0.48f, 1f)))
						.layer(LAYER_WORLD)
						.boundsRadius(2f)));
	}

	private static void addWall (Scene scene, String id, float x, float y, float z, float halfX, float halfY, float halfZ) {
		scene.addEntity(new Entity(id)
				.addComponent(new Transform()
						.position(x, y, z)
						.scale(halfX * 2f, halfY * 2f, halfZ * 2f))
				.addComponent(new BoxCollider()
						.halfExtents(0.5f, 0.5f, 0.5f)
						.friction(0.95f))
				.addComponent(new MeshRenderer()
						.mesh(Meshes.cube())
						.material(new Material().color(new Vec4(0.28f, 0.32f, 0.34f, 1f)))
						.layer(LAYER_WORLD)
						.boundsRadius(5f)));
	}

	private static void addDynamicBox (Scene scene, String id, float x, float y, float z, Vec4 color, Vec3 velocity) {
		addDynamicBox(scene, id, x, y, z, color, velocity, 0.1f);
	}

	private static void addDynamicBox (Scene scene, String id, float x, float y, float z, Vec4 color, Vec3 velocity, float restitution) {
		scene.addEntity(new Entity(id)
				.addComponent(new Transform()
						.position(x, y, z))
				.addComponent(new Rigidbody()
						.mass(1f)
						.velocity(velocity)
						.linearDamping(0.05f)
						.angularDamping(0.08f))
				.addComponent(new BoxCollider()
						.halfExtents(0.5f, 0.5f, 0.5f)
						.friction(0.9f)
						.restitution(restitution))
				.addComponent(new MeshRenderer()
						.mesh(Meshes.cube())
						.material(new Material().color(color))
						.layer(LAYER_PHYSICS)
						.boundsRadius(1f)));
	}

	private static final class PlatformBounceScript extends Script {
		private final float _minX;
		private final float _maxX;
		private Rigidbody _body;
		private Transform _transform;

		private PlatformBounceScript (float minX, float maxX) {
			_minX = minX;
			_maxX = maxX;
		}

		@Override
		public void init () {
			_body = entity().getComponent(Rigidbody.class);
			_transform = entity().getComponent(Transform.class);
		}

		@Override
		public void loop (float dtime) {
			if (_body == null || _transform == null) return;

			Vec3 position = _transform.position();
			Vec3 velocity = _body.velocity();
			if ((position.x <= _minX && velocity.x < 0f) || (position.x >= _maxX && velocity.x > 0f))
				_body.velocity(-velocity.x, velocity.y, velocity.z);
		}
	}

	private static final class PhysicsGridScript extends Script {
		@Override
		public void loop (float dtime) {
			for (int i = -4; i <= 4; i++) {
				DebugRenderer.line(new Vec3(i, 0f, -4f), new Vec3(i, 0f, 4f), new Vec4(0.18f, 0.25f, 0.28f, 1f));
				DebugRenderer.line(new Vec3(-4f, 0f, i), new Vec3(4f, 0f, i), new Vec4(0.18f, 0.25f, 0.28f, 1f));
			}
		}
	}

	private static final class PhysicsSceneControlScript extends Script {
		@Override
		public void loop (float dtime) {
			if (!Input.getKeyDown(KeyCode.R)) return;

			EngineContext context = EngineContext.current();
			context.window().setCursorDisabled();
			context.sceneManager().requestReplace(createScene());
		}
	}
}
