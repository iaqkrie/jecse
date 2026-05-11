package qchromatic.jecse.engine;

public final class SceneManager {
	private static final SceneManager DEFAULT = new SceneManager();

	private Scene _activeScene;
	private Scene _pendingScene;
	private boolean _pendingReplace;

	public Scene activeScene () {
		if (_activeScene == null) throw new RuntimeException("No active scene");
		return _activeScene;
	}

	public boolean hasActiveScene () {
		return _activeScene != null;
	}

	public void load (Scene scene) {
		if (scene == null) return;
		clearPendingChange();

		if (_activeScene == scene) {
			_activeScene.start();
			return;
		}

		if (_activeScene != null)
			_activeScene.stop();

		_activeScene = scene;
		_activeScene.start();
	}

	public void replace (Scene scene) {
		if (scene == null) return;
		clearPendingChange();

		if (_activeScene == scene) {
			_activeScene.start();
			return;
		}

		if (_activeScene != null) {
			Scene previousScene = _activeScene;
			_activeScene = null;
			previousScene.dispose();
		}

		_activeScene = scene;
		_activeScene.start();
	}

	public void requestLoad (Scene scene) {
		if (scene == null) return;

		_pendingScene = scene;
		_pendingReplace = false;
	}

	public void requestReplace (Scene scene) {
		if (scene == null) return;

		_pendingScene = scene;
		_pendingReplace = true;
	}

	public void applyPendingChanges () {
		if (_pendingScene == null) return;

		Scene scene = _pendingScene;
		boolean replace = _pendingReplace;
		clearPendingChange();

		if (replace)
			replace(scene);
		else
			load(scene);
	}

	public void unload () {
		if (_activeScene == null) return;
		clearPendingChange();

		_activeScene.stop();
		_activeScene = null;
	}

	public void disposeActiveScene () {
		if (_activeScene == null) return;
		clearPendingChange();

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

	public static void replaceScene (Scene scene) {
		currentManager().replace(scene);
	}

	public static void requestLoadScene (Scene scene) {
		currentManager().requestLoad(scene);
	}

	public static void requestReplaceScene (Scene scene) {
		currentManager().requestReplace(scene);
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

	private void clearPendingChange () {
		_pendingScene = null;
		_pendingReplace = false;
	}
}
