package qchromatic.jecse.graphics;

import qchromatic.jecse.math.Mat3f;
import qchromatic.jecse.math.Vec2;

import java.nio.file.Paths;

import static org.lwjgl.opengl.GL33.*;

public class GraphicsEnviroment {
	private static final float[] _GRAPHICS_DATA = {
			// pos     // tex
			-1f,  1f,  0f, 1f, // tl
			 1f,  1f,  1f, 1f, // tr
			-1f, -1f,  0f, 0f, // bl
			 1f, -1f,  1f, 0f  // br
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

		glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
		glEnableVertexAttribArray(0);

		glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
		glEnableVertexAttribArray(1);

		_mainEbo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _mainEbo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, _INDICES, GL_STATIC_DRAW);

		_shaders = new ShaderProgram(
				Paths.get("res/default-shaders/vertex.vert"),
				Paths.get("res/default-shaders/fragment.frag")
		);

		_shaders.use();
	}

	public static void clear () { glClear(GL_COLOR_BUFFER_BIT); }

	public static void render (Mat3f model, Mat3f view, Mat3f projection, Texture texture) {
		int modelUf = glGetUniformLocation(_shaders.getHandler(), "model");
		int viewUf = glGetUniformLocation(_shaders.getHandler(), "view");
		int projectionUf = glGetUniformLocation(_shaders.getHandler(), "projection");

		glUniformMatrix3fv(modelUf, false, model.getMatrix());
		glUniformMatrix3fv(viewUf, false, view.getMatrix());
		glUniformMatrix3fv(projectionUf, false, projection.getMatrix());

		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texture.texture);
		int textureUf = glGetUniformLocation(_shaders.getHandler(), "tex");
		glUniform1i(textureUf, 0);

		glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public static void finalise () {
		ShaderProgram.clearActiveProgram();
		_shaders.delete();

		glBindVertexArray(0);
		glDeleteVertexArrays(_mainVao);

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glDeleteBuffers(_mainVbo);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glDeleteBuffers(_mainEbo);
	}
}
