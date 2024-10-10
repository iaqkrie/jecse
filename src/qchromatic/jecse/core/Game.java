package qchromatic.jecse.core;

import qchromatic.jecse.graphics.GraphicsEnviroment;
import qchromatic.jecse.graphics.ShaderProgram;
import qchromatic.jecse.graphics.Window;
import qchromatic.jecse.math.Mat3f;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.lwjgl.opengl.GL20.*;

public final class Game {
	private final Window _window;

	public Game () {
		_window = new Window();
	}

	private void init () {
		String vss = "";
		String fss = "";

		try (FileReader vreader = new FileReader(new File("/home/iaqkrie/_/projects/jecse/res/default.vert"))) {
			int c;
			if ((c = vreader.read()) != -1) {
				vss += (char)c;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		try (FileReader freader = new FileReader(new File("/home/iaqkrie/_/projects/jecse/res/default.frag"))) {
			int c;
			if ((c = freader.read()) != -1) {
				fss += (char)c;
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		ShaderProgram program = new ShaderProgram(vss, fss);
		program.use();

		Mat3f matrix = new Mat3f();
		matrix.translate(1f, 0f);

		int matrixUniform = glGetUniformLocation(program.getProgram(), "matrix");
		glUniformMatrix4fv(matrixUniform, false, matrix.getMatrix());

		GraphicsEnviroment.init();
	}

	private void loop (long dtime) {
		GraphicsEnviroment.clear();
	}

	private void render () {
		GraphicsEnviroment.render();
	}

	private void finalise () {

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

		finalise();
	}
}
