package qchromatic.jecse.core;

import qchromatic.jecse.component.Camera;
import qchromatic.jecse.component.SpriteRenderer;
import qchromatic.jecse.component.Transform;
import qchromatic.jecse.ecs.Entity;
import qchromatic.jecse.graphics.GraphicsEnviroment;
import qchromatic.jecse.graphics.Window;
import qchromatic.jecse.math.Vec2f;
import qchromatic.jecse.system.RenderSystem;

public final class Game {
	private final Window _window;

	public Game () { _window = new Window(); }

	private void init () {
		TextureManager.init();
		GraphicsEnviroment.init();

		Scene mainScene = new Scene();

		Entity camera = new Entity();
		camera.addComponent(new Camera());

		Entity debug = new Entity();
		SpriteRenderer spriteRenderer = new SpriteRenderer();
		spriteRenderer.sprite = new Sprite(TextureManager.getTexture(0));
		debug.addComponent(spriteRenderer);

		mainScene.addEntity("Main camera", camera);
		mainScene.addEntity("Debug", debug);

		SceneManager.loadScene(mainScene);
	}

	private void loop (float dtime) {
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
