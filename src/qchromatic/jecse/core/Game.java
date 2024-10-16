package qchromatic.jecse.core;

import qchromatic.jecse.graphics.GraphicsEnviroment;
import qchromatic.jecse.graphics.Window;
import qchromatic.jecse.system.RenderSystem;
import qchromatic.jecse.system.ScriptSystem;

public final class Game {
	public static Window window;

	public Game () {
		window = new Window();

		TextureManager.init();
		GraphicsEnviroment.init();
	}

	private void init () {
		ScriptSystem.init();
	}

	private void loop (float dtime) {
		ScriptSystem.loop(dtime);

		InputInfo.update();
	}

	private void render () {
		GraphicsEnviroment.clear();
		RenderSystem.render();
	}

	private void finalise () {
		GraphicsEnviroment.finalise();

		TextureManager.unloadTextures();
	}

	public void run () {
		init();
		window.setIcon("res/jecse/jecse.png");
		window.show();

		long lastTime = System.nanoTime();
		while (!window.shouldClose()) {
			long currentTime = System.nanoTime();
			float dtime = (currentTime - lastTime) / 1_000_000_000f;
			lastTime = currentTime;

			window.pollEvents();

			loop(dtime);
			render();

			window.swapBuffers();
		}

		finalise();
	}
}
