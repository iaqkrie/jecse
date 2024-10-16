package qchromatic.jecse.core;

public class SceneManager {
	private static Scene _activeScene = null;

	public static void loadScene (Scene scene) {
		_activeScene = scene;
	}

	public static Scene getActiveScene () {
		return _activeScene;
	}
}
