package qchromatic.jecse.engine;

import qchromatic.jecse.graphics.Window;

import static org.lwjgl.glfw.GLFW.glfwGetTime;

public class Application {
	private final Window _window;

	public Application () { _window = new Window(); }
	public Application (String title) { _window = new Window(title); }
	public Application (int windowWidth, int windowHeight) { _window = new Window(windowWidth, windowHeight); }
	public Application (Window window) { _window = window; }

	public void run () {
		_window.show();

		float dTime;
		float lastFrameTime = (float) glfwGetTime();
		while (!_window.shouldClose()) {
			_window.clear();

			float currentTime = (float) glfwGetTime();
			SceneManager.getActiveScene().loop(currentTime - lastFrameTime);
			_window.update();

			lastFrameTime = currentTime;
		}

		_window.destroy();
	}

	public Window getWindow () { return _window; }
}
