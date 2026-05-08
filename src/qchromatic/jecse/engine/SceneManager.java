package qchromatic.jecse.engine;

public final class SceneManager {
	private static final SceneManager DEFAULT = new SceneManager();

	private Scene _activeScene;

	public Scene activeScene () {
		if (_activeScene == null) throw new RuntimeException("No active scene");
		return _activeScene;
	}

	public boolean hasActiveScene () {
		return _activeScene != null;
	}

	public void load (Scene scene) {
		if (scene == null) return;

		if (_activeScene == scene) {
			_activeScene.start();
			return;
		}

		if (_activeScene != null)
			_activeScene.stop();

		_activeScene = scene;
		_activeScene.start();
	}

	public void unload () {
		if (_activeScene == null) return;

		_activeScene.stop();
		_activeScene = null;
	}

	public void disposeActiveScene () {
		if (_activeScene == null) return;

		Scene scene = _activeScene;
		_activeScene = null;
		scene.dispose();
	}

	public static Scene getActiveScene () {
		return currentManager().activeScene();
	}

	public static boolean hasScene () {
		return currentManager().hasActiveScene();
	}

	public static void loadScene (Scene scene) {
		currentManager().load(scene);
	}

	public static void unloadScene () {
		currentManager().unload();
	}

	public static void disposeScene () {
		currentManager().disposeActiveScene();
	}

	private static SceneManager currentManager () {
		EngineContext context = EngineContext.currentOrNull();
		return context == null ? DEFAULT : context.sceneManager();
	}
}
