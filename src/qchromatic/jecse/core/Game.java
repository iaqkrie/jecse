package qchromatic.jecse.core;

import qchromatic.jecse.graphics.GraphicsEnviroment;
import qchromatic.jecse.graphics.Window;
import qchromatic.jecse.math.Mat3f;


public final class Game {
	private final Window _window;

	public Game () { _window = new Window(); }

	private void init () {
		GraphicsEnviroment.init();
	}

	private void loop (long dtime) {

	}

	private void render () {
		Mat3f model = new Mat3f();

		Mat3f view = new Mat3f();

		Mat3f projection = Mat3f.ortho(-16, 16, -9, 9);

		GraphicsEnviroment.render(model, view, projection);
	}

	private void finalise () {

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
