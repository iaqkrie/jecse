package qchromatic.jecse.core;

import qchromatic.jecse.graphics.Window;

public final class Game {
	private final Window _window;

	public Game () {
		_window = new Window();
	}

	private void init () {

	}

	private void loop (long dtime) {

	}

	private void render () {

	}

	public void run () {
		_window.show();
		init();

		long lastTime = System.nanoTime();
		while (!_window.shouldClose()) {
			long currentTime = System.nanoTime();
			long dtime = (currentTime - lastTime) / 1_000_000;
			lastTime = currentTime;

			_window.pollEvents();

			loop(dtime);
			render();

			_window.swapBuffers();
		}
	}
}
