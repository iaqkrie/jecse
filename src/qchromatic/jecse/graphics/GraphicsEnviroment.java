package qchromatic.jecse.graphics;

import qchromatic.jecse.math.Mat3f;

import java.nio.file.Paths;

import static org.lwjgl.opengl.GL33.*;

public class GraphicsEnviroment {
	private static final float[] _GRAPHICS_DATA = {
			// pos		// color		// tex
			-1f,  1f,  1f, 1f, 1f, 1f,  0f, 1f,	// tl
			 1f,  1f,  1f, 1f, 1f, 1f,  1f, 1f,	// tr
			-1f, -1f,  1f, 1f, 1f, 1f,  0f, 0f,	// bl
			 1f, -1f,  1f, 1f, 1f, 1f,  1f, 0f	// br
	};

	private static final int[] _INDICES = {
			0, 1, 2,
			1, 3, 2
	};

	private static int _mainVao;

	private static int _mainVbo;
	private static int _mainEbo;

	private static ShaderProgram _shaders;

	public static void init () {
		glClearColor(0f, 0f, 0f, 1f);

		_mainVao = glGenVertexArrays();
		glBindVertexArray(_mainVao);

		_mainVbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, _mainVbo);
		glBufferData(GL_ARRAY_BUFFER, _GRAPHICS_DATA, GL_STATIC_DRAW);

		glVertexAttribPointer(0, 2, GL_FLOAT, false, 8 * Float.BYTES, 0);
		glEnableVertexAttribArray(0);

		glVertexAttribPointer(1, 4, GL_FLOAT, false, 8 * Float.BYTES, 2 * Float.BYTES);
		glEnableVertexAttribArray(1);

		glVertexAttribPointer(2, 2, GL_FLOAT, false, 8 * Float.BYTES, 6 * Float.BYTES);
		//glEnableVertexAttribArray(2);

		_mainEbo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _mainEbo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, _INDICES, GL_STATIC_DRAW);

		_shaders = new ShaderProgram(
				Paths.get("res/default-shaders/vertex.vert"),
				Paths.get("res/default-shaders/fragment.frag")
		);

		_shaders.use();
	}

	public static void render (Mat3f model, Mat3f view, Mat3f projection) {
		glClear(GL_COLOR_BUFFER_BIT);

		int modelUf = glGetUniformLocation(_shaders.getHandler(), "model");
		int viewUf = glGetUniformLocation(_shaders.getHandler(), "view");
		int projectionUf = glGetUniformLocation(_shaders.getHandler(), "projection");

		glUniformMatrix3fv(modelUf, false, model.getMatrix());
		glUniformMatrix3fv(viewUf, false, view.getMatrix());
		glUniformMatrix3fv(projectionUf, false, projection.getMatrix());

		glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
	}
}
