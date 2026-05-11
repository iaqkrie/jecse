package common.showcase;

import qchromatic.jecse.common.Vec2;
import qchromatic.jecse.common.Vec3;
import qchromatic.jecse.common.Vec4;
import qchromatic.jecse.component.Camera;
import qchromatic.jecse.component.MeshRenderer;
import qchromatic.jecse.component.Script;
import qchromatic.jecse.component.Transform;
import qchromatic.jecse.core.Entity;
import qchromatic.jecse.engine.Application;
import qchromatic.jecse.engine.EngineConfig;
import qchromatic.jecse.engine.Input;
import qchromatic.jecse.engine.KeyCode;
import qchromatic.jecse.engine.Material;
import qchromatic.jecse.engine.Mesh;
import qchromatic.jecse.engine.Meshes;
import qchromatic.jecse.engine.Scene;
import qchromatic.jecse.graphics.Window;
import qchromatic.jecse.system.RenderSystem;
import qchromatic.jecse.system.ScriptSystem;

public final class Interactive2DScene {
	private static final int LAYER_UI = 1;
	private static final float ORTHO_SIZE = 4.5f;

	public static void main (String[] args) {
		EngineConfig config = new EngineConfig()
				.clearColor(0.07f, 0.08f, 0.10f, 1f)
				.autoUpdateCameraAspect(true)
				.debugOpenGLErrors(false);

		Application app = new Application("jecse interactive 2d", config);
		app.getWindow().setCursorNormal();
		app.context().sceneManager().load(createScene());
		app.run();
	}

	private static Scene createScene () {
		Scene scene = new Scene();
		scene.addSystem(new ScriptSystem());
		scene.addSystem(new RenderSystem()
				.ambientColor(new Vec4(1f, 1f, 1f, 1f)));

		Camera camera = new Camera()
				.ortho(true)
				.orthoSize(ORTHO_SIZE)
				.near(0.1f)
				.far(40f)
				.cullingMask(LAYER_UI)
				.priority(10);
		Transform cameraTransform = new Transform()
				.position(0f, 0f, 10f);
		scene.addEntity(new Entity("ortho-camera")
				.addComponent(cameraTransform)
				.addComponent(camera));

		scene.addEntity(new Entity("mouse-space")
				.addComponent(new MouseWorldTracker(camera, cameraTransform)));

		addBackground(scene);
		addColorButton(scene);
		addToggleTriangle(scene);
		addPulseCircle(scene);
		addSlider(scene);

		return scene;
	}

	private static void addBackground (Scene scene) {
		scene.addEntity(new Entity("background-panel")
				.addComponent(new Transform()
						.position(0f, 0f, -0.2f)
						.scale(8.2f, 6.6f, 1f))
				.addComponent(new MeshRenderer()
						.mesh(Meshes.quad())
						.material(new Material().color(new Vec4(0.12f, 0.14f, 0.17f, 1f)))
						.layer(LAYER_UI)
						.renderQueue(-100)
						.boundsRadius(6f)));
	}

	private static void addColorButton (Scene scene) {
		Material material = new Material().color(new Vec4(0.24f, 0.62f, 1f, 1f));
		scene.addEntity(new Entity("color-button")
				.addComponent(new Transform()
						.position(-2.7f, 1.25f, 0f)
						.scale(1.25f, 1.25f, 1f))
				.addComponent(new MeshRenderer()
						.mesh(Meshes.quad())
						.material(material)
						.layer(LAYER_UI)
						.boundsRadius(1f))
				.addComponent(new ColorButtonScript(material)));
	}

	private static void addToggleTriangle (Scene scene) {
		Material material = new Material().color(new Vec4(1f, 0.62f, 0.24f, 1f));
		scene.addEntity(new Entity("toggle-triangle")
				.addComponent(new Transform()
						.position(0f, 1.25f, 0f)
						.scale(1.45f, 1.45f, 1f))
				.addComponent(new MeshRenderer()
						.mesh(Meshes.triangle())
						.material(material)
						.layer(LAYER_UI)
						.boundsRadius(1f))
				.addComponent(new ToggleSpinScript(material)));
	}

	private static void addPulseCircle (Scene scene) {
		Material material = new Material().color(new Vec4(0.42f, 0.95f, 0.52f, 1f));
		scene.addEntity(new Entity("pulse-circle")
				.addComponent(new Transform()
						.position(2.7f, 1.25f, 0f)
						.scale(1.25f, 1.25f, 1f))
				.addComponent(new MeshRenderer()
						.mesh(circleMesh(40))
						.material(material)
						.layer(LAYER_UI)
						.boundsRadius(1f))
				.addComponent(new PulseCircleScript(material)));
	}

	private static void addSlider (Scene scene) {
		Material track = new Material().color(new Vec4(0.22f, 0.24f, 0.28f, 1f));
		Material fill = new Material().color(new Vec4(0.82f, 0.45f, 1f, 1f));
		Transform fillTransform = new Transform()
				.position(-1.8f, -1.35f, 0.04f)
				.scale(0.4f, 0.34f, 1f);

		scene.addEntity(new Entity("slider-track")
				.addComponent(new Transform()
						.position(0f, -1.35f, 0f)
						.scale(3.6f, 0.38f, 1f))
				.addComponent(new MeshRenderer()
						.mesh(Meshes.quad())
						.material(track)
						.layer(LAYER_UI)
						.boundsRadius(2f))
				.addComponent(new SliderTrackScript(fillTransform)));

		scene.addEntity(new Entity("slider-fill")
				.addComponent(fillTransform)
				.addComponent(new MeshRenderer()
						.mesh(Meshes.quad())
						.material(fill)
						.layer(LAYER_UI)
						.renderQueue(1)
						.boundsRadius(2f)));
	}

	private static Mesh circleMesh (int segments) {
		float[] vertices = new float[(segments + 1) * 8];
		int vertex = 0;
		writeVertex(vertices, vertex++, 0f, 0f, 0.5f, 0.5f);
		for (int i = 0; i < segments; i++) {
			float angle = (float) (Math.PI * 2.0 * i / segments);
			float x = (float) Math.cos(angle) * 0.5f;
			float y = (float) Math.sin(angle) * 0.5f;
			writeVertex(vertices, vertex++, x, y, x + 0.5f, y + 0.5f);
		}

		int[] triangles = new int[segments * 3];
		int index = 0;
		for (int i = 0; i < segments; i++) {
			triangles[index++] = 0;
			triangles[index++] = i + 1;
			triangles[index++] = i == segments - 1 ? 1 : i + 2;
		}

		return new Mesh(vertices, triangles, 8);
	}

	private static void writeVertex (float[] vertices, int index, float x, float y, float u, float v) {
		int offset = index * 8;
		vertices[offset] = x;
		vertices[offset + 1] = y;
		vertices[offset + 2] = 0f;
		vertices[offset + 3] = u;
		vertices[offset + 4] = v;
		vertices[offset + 5] = 0f;
		vertices[offset + 6] = 0f;
		vertices[offset + 7] = 1f;
	}

	private abstract static class ClickableScript extends Script {
		private final float _halfWidth;
		private final float _halfHeight;
		private Transform _transform;
		private boolean _hovered;

		private ClickableScript (float halfWidth, float halfHeight) {
			_halfWidth = halfWidth;
			_halfHeight = halfHeight;
		}

		@Override
		public void init () {
			_transform = entity().getComponent(Transform.class);
		}

		@Override
		public void loop (float dtime) {
			if (_transform == null) return;

			Vec2 mouse = MouseWorldTracker.mouseWorld();
			Vec3 position = _transform.position();
			Vec3 scale = _transform.scale();
			_hovered = mouse.x >= position.x - _halfWidth * scale.x
					&& mouse.x <= position.x + _halfWidth * scale.x
					&& mouse.y >= position.y - _halfHeight * scale.y
					&& mouse.y <= position.y + _halfHeight * scale.y;

			onFrame(dtime, _transform, _hovered);
			if (_hovered && Input.getMouseButtonDown(KeyCode.MB_1))
				onClick(_transform);
		}

		protected abstract void onFrame (float dtime, Transform transform, boolean hovered);

		protected abstract void onClick (Transform transform);
	}

	private static final class ColorButtonScript extends ClickableScript {
		private final Material _material;
		private final Vec4[] _colors;
		private int _index;
		private float _pulse;

		private ColorButtonScript (Material material) {
			super(0.5f, 0.5f);
			_material = material;
			_colors = new Vec4[] {
					new Vec4(0.24f, 0.62f, 1f, 1f),
					new Vec4(1f, 0.35f, 0.38f, 1f),
					new Vec4(0.92f, 0.82f, 0.26f, 1f)
			};
		}

		@Override
		protected void onFrame (float dtime, Transform transform, boolean hovered) {
			_pulse = Math.max(0f, _pulse - dtime * 4f);
			float scale = hovered ? 1.1f : 1f;
			scale += _pulse * 0.25f;
			transform.scale(1.25f * scale, 1.25f * scale, 1f);
		}

		@Override
		protected void onClick (Transform transform) {
			_index = (_index + 1) % _colors.length;
			_material.color(_colors[_index]);
			_pulse = 1f;
		}
	}

	private static final class ToggleSpinScript extends ClickableScript {
		private final Material _material;
		private boolean _spinning = true;
		private float _angle;

		private ToggleSpinScript (Material material) {
			super(0.55f, 0.55f);
			_material = material;
		}

		@Override
		protected void onFrame (float dtime, Transform transform, boolean hovered) {
			if (_spinning)
				_angle += dtime * 120f;

			transform.rotation(0f, 0f, _angle);
			_material.color(hovered ? new Vec4(1f, 0.78f, 0.34f, 1f) : new Vec4(1f, 0.62f, 0.24f, 1f));
		}

		@Override
		protected void onClick (Transform transform) {
			_spinning = !_spinning;
		}
	}

	private static final class PulseCircleScript extends ClickableScript {
		private final Material _material;
		private float _targetScale = 1.25f;
		private float _time;

		private PulseCircleScript (Material material) {
			super(0.5f, 0.5f);
			_material = material;
		}

		@Override
		protected void onFrame (float dtime, Transform transform, boolean hovered) {
			_time += dtime;
			float wave = (float) Math.sin(_time * 5f) * 0.04f;
			float scale = _targetScale + wave + (hovered ? 0.12f : 0f);
			transform.scale(scale, scale, 1f);
			_material.color(hovered ? new Vec4(0.58f, 1f, 0.68f, 1f) : new Vec4(0.42f, 0.95f, 0.52f, 1f));
		}

		@Override
		protected void onClick (Transform transform) {
			_targetScale = _targetScale > 1.4f ? 1.05f : _targetScale + 0.2f;
		}
	}

	private static final class SliderTrackScript extends ClickableScript {
		private final Transform _fill;
		private float _value = 0.18f;

		private SliderTrackScript (Transform fill) {
			super(0.5f, 0.5f);
			_fill = fill;
		}

		@Override
		protected void onFrame (float dtime, Transform transform, boolean hovered) {
			if (hovered && Input.getMouseButton(KeyCode.MB_1))
				setValueFromMouse(transform);

			_fill.scale(3.6f * _value, 0.34f, 1f);
			_fill.position(transform.position().x - 1.8f + 1.8f * _value, transform.position().y, 0.04f);
		}

		@Override
		protected void onClick (Transform transform) {
			setValueFromMouse(transform);
		}

		private void setValueFromMouse (Transform transform) {
			float x = MouseWorldTracker.mouseWorld().x;
			float left = transform.position().x - 1.8f;
			_value = Math.max(0.05f, Math.min(1f, (x - left) / 3.6f));
		}
	}

	private static final class MouseWorldTracker extends Script {
		private static Vec2 _mouseWorld = new Vec2();

		private final Camera _camera;
		private final Transform _cameraTransform;

		private MouseWorldTracker (Camera camera, Transform cameraTransform) {
			_camera = camera;
			_cameraTransform = cameraTransform;
		}

		@Override
		public void loop (float dtime) {
			Window window = qchromatic.jecse.engine.EngineContext.current().window();
			Vec2 mouse = Input.getMousePosition();
			float aspect = window.aspectRatio();
			float worldX = (mouse.x / Math.max(1f, window.width()) * 2f - 1f) * _camera.orthoSize() * aspect;
			float worldY = (1f - mouse.y / Math.max(1f, window.height()) * 2f) * _camera.orthoSize();
			Vec3 cameraPosition = _cameraTransform.position();

			_mouseWorld = new Vec2(worldX + cameraPosition.x, worldY + cameraPosition.y);
		}

		private static Vec2 mouseWorld () {
			return new Vec2(_mouseWorld);
		}
	}
}
