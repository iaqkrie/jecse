package qchromatic.jecse.core;

import qchromatic.jecse.graphics.GraphicsEnviroment;
import qchromatic.jecse.graphics.Window;

public final class Game {
	private final Window _window;

	public Game () { _window = new Window(); }

	private void init () {
		GraphicsEnviroment.init();
	}

	private void loop (long dtime) {
	}

	private void render () {
		GraphicsEnviroment.clear();
	}

	private void finalise () {
		GraphicsEnviroment.finalise();
	}

	public void run () {
		init();
		_window.show();

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

		finalise();
	}
}
