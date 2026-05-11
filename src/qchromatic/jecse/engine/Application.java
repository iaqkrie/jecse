package qchromatic.jecse.engine;

import qchromatic.jecse.component.Camera;
import qchromatic.jecse.core.Entity;
import qchromatic.jecse.graphics.Window;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public final class Application {
	private final EngineContext _context;
	private final Window _window;

	public Application () { this(createContext(800, 450, "jecse app")); }
	public Application (EngineConfig config) { this(createContext(800, 450, "jecse app", config)); }
	public Application (String title) { this(createContext(800, 450, title)); }
	public Application (String title, EngineConfig config) { this(createContext(800, 450, title, config)); }
	public Application (int windowWidth, int windowHeight) { this(createContext(windowWidth, windowHeight, "jecse app")); }
	public Application (int windowWidth, int windowHeight, EngineConfig config) { this(createContext(windowWidth, windowHeight, "jecse app", config)); }
	public Application (Window window) { this(new EngineContext(window, window.input(), new AssetManager(), new SceneManager(), new EngineConfig(), new Time())); }
	public Application (EngineContext context) {
		if (context == null)
			throw new IllegalArgumentException("Engine context cannot be null");

		_context = context;
		_window = context.window();
		_context.use();
		_window.addResizeListener((window, width, height) -> updateCameraAspect());
	}

	public void run () {
		run(null);
	}

	public void run (Scene startScene) {
		_context.use();

		try {
			if (startScene != null)
				_context.sceneManager().load(startScene);
			else if (!_context.sceneManager().hasActiveScene())
				throw new RuntimeException("No start scene");

			updateCameraAspect();
			_window.show();

			_context.time().reset((float) glfwGetTime());
			while (!_window.shouldClose()) {
				_window.clear(_context.config().clearColor());

				float currentTime = (float) glfwGetTime();
				_context.time().update(currentTime);
				_context.sceneManager().activeScene().loop(_context.time().deltaTime());
				_context.sceneManager().applyPendingChanges();
				updateCameraAspect();
				_context.input().update();
				_window.update();
			}
		} finally {
			_context.destroy();
		}
	}

	public EngineContext context () { return _context; }

	public Window getWindow () { return _window; }

	private static EngineContext createContext (int width, int height, String title) {
		return createContext(width, height, title, new EngineConfig());
	}

	private static EngineContext createContext (int width, int height, String title, EngineConfig config) {
		InputState input = new InputState();
		Window window = new Window(width, height, title, input);
		return new EngineContext(window, input, new AssetManager(), new SceneManager(), config, new Time());
	}

	private void updateCameraAspect () {
		if (!_context.config().autoUpdateCameraAspect() || !_context.sceneManager().hasActiveScene())
			return;

		for (Entity entity : _context.sceneManager().activeScene().entities()) {
			Camera camera = entity.getComponent(Camera.class);
			if (camera != null)
				camera.aspectRatio(_window.aspectRatio());
		}
	}
}
