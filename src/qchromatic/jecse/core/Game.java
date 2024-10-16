package qchromatic.jecse.core;

import qchromatic.jecse.graphics.GraphicsEnviroment;
import qchromatic.jecse.graphics.Window;
import qchromatic.jecse.system.TextureManager;

public final class Game {
	private final Window _window;

	public Game () { _window = new Window(); }

	private void init () {
		TextureManager.init();
		GraphicsEnviroment.init();
	}

	private void loop (float dtime) {
		InputInfo.update();
	}

	private void render () {
		GraphicsEnviroment.clear();
	}

	private void finalise () {
		GraphicsEnviroment.finalise();

		TextureManager.unloadTextures();
	}

	public void run () {
		init();
		_window.show();

		long lastTime = System.nanoTime();
		while (!_window.shouldClose()) {
			long currentTime = System.nanoTime();
			float dtime = (currentTime - lastTime) / 1_000_000_000f;
			lastTime = currentTime;

			_window.pollEvents();

			loop(dtime);
			render();

			_window.swapBuffers();
		}

		finalise();
	}
}
