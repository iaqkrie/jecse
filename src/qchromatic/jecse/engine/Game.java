package qchromatic.jecse.engine;

import qchromatic.jecse.graphics.Window;

public class Game {
	private Window _window;

	public Game (Window window) {
		_window = window;
	}
	public Game () {
		this(new Window());
	}

	public void run () {
		_window.show();

		while (!_window.shouldClose()) {
			_window.clear();
			SceneManager.getActiveScene().loop(1f);
			_window.update();
		}

		_window.destroy();
	}
}
