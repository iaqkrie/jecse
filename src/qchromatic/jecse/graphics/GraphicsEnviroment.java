package qchromatic.jecse.graphics;

import static org.lwjgl.opengl.GL20.*;

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

	private static int _mainVbo;
	private static int _mainEbo;

	public static void init () {
		glClearColor(0f, 0f, 0f, 1f);

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
	}

	public static void clear () {
		glClear(GL_COLOR_BUFFER_BIT);
	}

	// TODO
	public static void render () {
		glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
	}
}
