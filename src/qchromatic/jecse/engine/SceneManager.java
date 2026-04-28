package qchromatic.jecse.engine;

public final class SceneManager {
	private SceneManager () {}

	private static Scene _activeScene;

	public static Scene getActiveScene () {
		if (_activeScene == null) throw new RuntimeException("No active scene");

		return _activeScene;
	}

	public static void loadScene (Scene scene) {
		if (scene == null) return;

		if (_activeScene != null)
			_activeScene.destroy();

		_activeScene = scene;
		_activeScene.init();
	}

	public static void unloadScene () {
		if (_activeScene == null) return;

		_activeScene.destroy();
		_activeScene = null;
	}
}
