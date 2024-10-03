package qchromatic.jecse.core;

import qchromatic.jecse.graphics.Window;

import static org.lwjgl.opengl.GL20.*;

public final class Game {
	private final Window _window;

	public Game () {
		_window = new Window();
	}

	private void init () {

	}

	private void loop () {

	}

	private void render () {
		glBegin(GL_TRIANGLES);
			glColor3f(0f, 0f, 1f);
			glVertex2f(0f, 0f);

			glColor3f(1f, 0f, 0f);
			glVertex2f(.5f, 0f);

			glColor3f(0f, 1f, 0f);
			glVertex2f(0f, .5f);
		glEnd();
	}

	public void run () {
		init();

		while (!_window.shouldClose()) {
			_window.pollEvents();

			loop();
			render();

			_window.swapBuffers();
		}
	}
}
