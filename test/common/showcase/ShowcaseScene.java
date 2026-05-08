package common.showcase;

import qchromatic.jecse.common.Quaternion;
import qchromatic.jecse.common.Vec3;
import qchromatic.jecse.common.Vec4;
import qchromatic.jecse.component.Camera;
import qchromatic.jecse.component.DirectionalLight;
import qchromatic.jecse.component.MeshRenderer;
import qchromatic.jecse.component.Script;
import qchromatic.jecse.component.Transform;
import qchromatic.jecse.core.Entity;
import qchromatic.jecse.engine.Application;
import qchromatic.jecse.engine.DebugRenderer;
import qchromatic.jecse.engine.EngineConfig;
import qchromatic.jecse.engine.Material;
import qchromatic.jecse.engine.Meshes;
import qchromatic.jecse.engine.Scene;
import qchromatic.jecse.script.FreecamController;
import qchromatic.jecse.system.DebugRenderSystem;
import qchromatic.jecse.system.RenderSystem;
import qchromatic.jecse.system.ScriptSystem;

public class ShowcaseScene {
	// Основной слой сцены: камера будет видеть все MeshRenderer с этим layer bit.
	private static final int LAYER_WORLD = 1;

	// Дополнительный слой для объектов, которые демонстрируют culling mask.
	private static final int LAYER_DEBUG = 1 << 1;

	// Слой, который камера намеренно не видит: объект существует, но не попадает в render queue.
	private static final int LAYER_HIDDEN = 1 << 2;

	public static void main (String[] args) {
		// EngineConfig показывает, что runtime теперь можно настраивать до запуска приложения.
		EngineConfig config = new EngineConfig()
				// Цвет очистки экрана используется Window.clear(config.clearColor()) каждый кадр.
				.clearColor(0.06f, 0.08f, 0.10f, 1f)
				// fixedDeltaTime пока нужен как часть Time API для будущих fixed-update систем.
				.fixedDeltaTime(1f / 60f)
				// timeScale влияет на Time.deltaTime, который получает Scene.loop().
				.timeScale(1f)
				// При resize окна Application автоматически обновляет aspectRatio всех камер.
				.autoUpdateCameraAspect(true)
				// OpenGL ошибки можно включить для отладки RenderSystem.
				.debugOpenGLErrors(false);

		// Application создает Window, EngineContext, InputState, AssetManager, SceneManager и Time.
		Application app = new Application("jecse showcase", config);

		// Scene хранит entities, systems, queries и безопасно применяет structural changes между системами.
		Scene scene = new Scene();

		// Сначала добавляем ScriptSystem: у него order 0, значит scripts выполняются до рендера.
		scene.addSystem(new ScriptSystem());

		// RenderSystem имеет order 1000: собирает render queue, сортирует draw calls и рисует меши.
		scene.addSystem(new RenderSystem()
				// Ambient color попадает в basic shader и смешивается с directional light.
				.ambientColor(new Vec4(0.18f, 0.20f, 0.24f, 1f)));

		// DebugRenderSystem имеет order 1100: рисует линии после основного рендера.
		scene.addSystem(new DebugRenderSystem());

		// DirectionalLight демонстрирует компонент света и lighting uniforms в базовом shader.
		scene.addEntity(new Entity("sun")
				.addComponent(new DirectionalLight()
						// Направление света нормализуется внутри компонента.
						.direction(-0.4f, -1f, -0.3f)
						// Теплый оттенок света виден на кубах с нормалями.
						.color(new Vec4(1f, 0.92f, 0.78f, 1f))
						// Intensity масштабирует цвет света.
						.intensity(1.15f)));

		// Камера показывает Transform + Camera + Script на одной entity.
		scene.addEntity(new Entity("camera")
				.addComponent(new Transform()
						// Камера стоит перед сценой и смотрит в сторону -Z по умолчанию.
						.position(0f, 2.2f, 7f)
						// Небольшой pitch вниз, чтобы видеть пол и иерархию объектов.
						.rotation(Quaternion.euler(-12f, 0f, 0f)))
				.addComponent(new Camera()
						// Perspective camera.
						.fov(60f)
						// Камера видит только WORLD и DEBUG слои.
						.cullingMask(LAYER_WORLD | LAYER_DEBUG)
						// Приоритет нужен, если в сцене несколько камер.
						.priority(10)
						// Near/far используются RenderSystem для простой sphere frustum culling.
						.near(0.1f)
						.far(80f))
				.addComponent(new FreecamController()
						// WASD/Space/Shift двигают камеру через Input facade.
						.speed(6f)
						// Mouse delta вращает камеру.
						.sensitivity(20f)));

		// Материал главного куба: generic uniforms позволяют задавать u_color без специального класса.
		Material rootMaterial = new Material()
				.color(new Vec4(0.25f, 0.65f, 1f, 1f))
				// Пользовательский uniform не используется basic shader, но показывает API материала.
				.set("u_showcaseValue", 1f);

		// Root transform сохранен в переменную, чтобы ниже подвесить child transform.
		Transform rootTransform = new Transform()
				.position(-1.4f, 0.7f, 0f)
				.scale(1.15f, 1.15f, 1.15f);

		// Root entity демонстрирует MeshRenderer, material, render layer, boundsRadius и script lifecycle.
		Entity rootCube = new Entity("root-cube")
				.addComponent(rootTransform)
				.addComponent(new MeshRenderer()
						// Cube теперь содержит position/uv/normal, поэтому реагирует на DirectionalLight.
						.mesh(Meshes.cube())
						.material(rootMaterial)
						// WORLD слой видим текущей камерой.
						.layer(LAYER_WORLD)
						// renderQueue участвует в сортировке render queue.
						.renderQueue(0)
						// boundsRadius участвует в простой проверке видимости.
						.boundsRadius(1.2f))
				.addComponent(new SpinScript(0f, 35f, 0f));
		scene.addEntity(rootCube);

		// Child transform демонстрирует parent/children: он будет двигаться и вращаться вместе с root.
		Transform childTransform = new Transform()
				// Локальная позиция относительно rootTransform.
				.position(1.6f, 0f, 0f)
				.scale(0.45f, 0.45f, 0.45f)
				// Привязка к родителю включает пересчет world matrix через dirty propagation.
				.parent(rootTransform);

		// Child cube использует другой материал и renderQueue, но тот же Meshes.cube().
		scene.addEntity(new Entity("child-cube")
				.addComponent(childTransform)
				.addComponent(new MeshRenderer()
						.mesh(Meshes.cube())
						.material(new Material().color(new Vec4(1f, 0.44f, 0.24f, 1f)))
						.layer(LAYER_WORLD)
						.renderQueue(1)
						.boundsRadius(0.55f))
				.addComponent(new SpinScript(60f, 0f, 120f)));

		// Пол сделан из quad: демонстрирует масштаб Transform и отдельный renderQueue.
		scene.addEntity(new Entity("floor")
				.addComponent(new Transform()
						.position(0f, -0.05f, 0f)
						.rotation(Quaternion.euler(90f, 0f, 0f))
						.scale(7f, 7f, 1f))
				.addComponent(new MeshRenderer()
						.mesh(Meshes.quad())
						.material(new Material().color(new Vec4(0.18f, 0.22f, 0.22f, 1f)))
						.layer(LAYER_WORLD)
						.renderQueue(-10)
						.boundsRadius(5f)));

		// DEBUG слой видим камерой и показывает, что cullingMask может включать несколько слоев.
		scene.addEntity(new Entity("debug-layer-triangle")
				.addComponent(new Transform()
						.position(1.8f, 0.8f, -0.3f)
						.rotation(Quaternion.euler(0f, 35f, 0f))
						.scale(1.1f, 1.1f, 1.1f))
				.addComponent(new MeshRenderer()
						.mesh(Meshes.triangle())
						.material(new Material().color(new Vec4(0.85f, 1f, 0.35f, 1f)))
						.layer(LAYER_DEBUG)
						.renderQueue(2)
						.boundsRadius(0.9f)));

		// HIDDEN слой не входит в camera.cullingMask(), поэтому объект существует, но не рисуется.
		scene.addEntity(new Entity("hidden-cube")
				.addComponent(new Transform()
						.position(0f, 1.2f, -2.5f)
						.scale(0.8f, 0.8f, 0.8f))
				.addComponent(new MeshRenderer()
						.mesh(Meshes.cube())
						.material(new Material().color(new Vec4(1f, 0f, 1f, 1f)))
						.layer(LAYER_HIDDEN)
						.boundsRadius(1f)));

		// Очень дальний куб демонстрирует frustum/far culling: камера его не должна отправлять в draw call.
		scene.addEntity(new Entity("culled-by-far-plane")
				.addComponent(new Transform()
						.position(0f, 0f, -120f))
				.addComponent(new MeshRenderer()
						.mesh(Meshes.cube())
						.material(new Material().color(new Vec4(1f, 1f, 1f, 1f)))
						.layer(LAYER_WORLD)
						.boundsRadius(1f)));

		// DebugGuidesScript каждый кадр добавляет линии, axes и bounds в DebugRenderer.
		scene.addEntity(new Entity("debug-guides")
				.addComponent(new DebugGuidesScript(rootTransform, childTransform)));

		// SceneManager.loadScene стартует scene, но не уничтожает ее данные при обычной выгрузке.
		app.context().sceneManager().load(scene);

		// run() использует уже загруженную сцену, обновляет Time, InputState, Scene.loop и Window.
		app.run();
	}

	private static final class SpinScript extends Script {
		// Скорость вращения по Euler-осям в градусах в секунду.
		private final Vec3 _degreesPerSecond;

		// Ссылка на Transform кешируется в init(), чтобы не искать компонент каждый кадр.
		private Transform _transform;

		// Текущий угол хранится отдельно, потому что Transform.rotation() возвращает защитную копию.
		private Vec3 _angle;

		private SpinScript (float x, float y, float z) {
			_degreesPerSecond = new Vec3(x, y, z);
			_angle = new Vec3();
		}

		@Override
		public void init () {
			// init() вызывается один раз ScriptSystem, когда script зарегистрирован в активной сцене.
			_transform = entity().getComponent(Transform.class);
		}

		@Override
		public void start () {
			// start() вызывается при старте сцены; здесь удобно сбросить runtime-состояние.
			_angle = new Vec3();
		}

		@Override
		public void loop (float dtime) {
			// Если Transform удалили structural change-ом, script не должен падать.
			if (_transform == null) return;

			// dtime уже учитывает EngineConfig.timeScale через Time.update().
			_angle = _angle.added(_degreesPerSecond.multiplied(dtime));

			// Transform помечает себя dirty и проталкивает dirty flag в дочерние Transform.
			_transform.rotation(_angle.x, _angle.y, _angle.z);
		}
	}

	private static final class DebugGuidesScript extends Script {
		// Root Transform нужен для DebugRenderer.axis().
		private final Transform _root;

		// Child Transform нужен, чтобы показать worldPosition дочернего объекта.
		private final Transform _child;

		private DebugGuidesScript (Transform root, Transform child) {
			_root = root;
			_child = child;
		}

		@Override
		public void loop (float dtime) {
			// Мировая сетка показывает работу DebugRenderSystem с GL_LINES.
			DebugRenderer.line(new Vec3(-4f, 0f, 0f), new Vec3(4f, 0f, 0f), new Vec4(0.5f, 0.15f, 0.15f, 1f));
			DebugRenderer.line(new Vec3(0f, 0f, -4f), new Vec3(0f, 0f, 4f), new Vec4(0.15f, 0.35f, 0.55f, 1f));

			// Axis рисует локальные направления Transform с учетом world rotation.
			DebugRenderer.axis(_root, 1.5f);

			// Bounds рисует AABB вокруг root cube как debug helper.
			DebugRenderer.bounds(_root.worldPosition(), new Vec3(0.75f, 0.75f, 0.75f), new Vec4(0f, 1f, 0.6f, 1f));

			// Линия root -> child показывает parent/child transform hierarchy в world space.
			DebugRenderer.line(_root.worldPosition(), _child.worldPosition(), new Vec4(1f, 1f, 0f, 1f));
		}
	}
}
