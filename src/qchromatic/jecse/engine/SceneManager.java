package qchromatic.jecse.engine;

public final class SceneManager {
	private static Scene _activeScene;

	public static Scene getActiveScene () {
		if (_activeScene == null) throw new RuntimeException("No active scene");

		return _activeScene;
	}

	public static void loadScene (Scene scene) {
		if (scene == null) return;

		scene.init();
		_activeScene = scene;
	}
}
