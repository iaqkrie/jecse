package qchromatic.jecse.graphics;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {
	int vertexShader;
	int fragmentShader;

	int program;

	public ShaderProgram (String vertexShaderSource, String fragmentShaderSource) {
		vertexShader = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertexShader, vertexShaderSource);
		glCompileShader(vertexShader);

		fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragmentShader, fragmentShaderSource);
		glCompileShader(fragmentShader);

		program = glCreateProgram();
		glAttachShader(program, vertexShader);
		glAttachShader(program, fragmentShader);
		glLinkProgram(program);

		glDetachShader(program, vertexShader);
		glDetachShader(program, fragmentShader);
	}
}
