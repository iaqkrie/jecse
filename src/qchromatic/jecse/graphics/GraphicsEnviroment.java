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

		ShaderProgram shaders = new ShaderProgram(
				Paths.get("D:\\~iaq\\projects\\jecse\\res\\default-shaders\\vertex.vert"),
				Paths.get("D:\\~iaq\\projects\\jecse\\res\\default-shaders\\fragment.frag")
		);

		Mat3f matrix = new Mat3f();
		matrix.translate(1f, 0f);
		matrix.scale(0.5f, 0.5f);

		shaders.use();

		int matrixUniform = glGetUniformLocation(shaders.getHandler(), "matrix");
		glUniformMatrix3fv(matrixUniform, false, matrix.getMatrix());
	}

	public static void render () {
		glClear(GL_COLOR_BUFFER_BIT);
		glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
	}
}
