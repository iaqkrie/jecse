package qchromatic.jecse.graphics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL20.*;

public class ShaderProgram {
	int handler;

	public ShaderProgram (Path vertexShaderPath, Path fragmentShaderPath) {
		try {
			String vss = new String(Files.readAllBytes(vertexShaderPath));
			String fss = new String(Files.readAllBytes(fragmentShaderPath));

			createShaders(vss, fss);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public ShaderProgram (String vertexShaderSource, String fragmentShaderSource) {
		createShaders(vertexShaderSource, fragmentShaderSource);
	}

	private void createShaders (String vertexShaderSource, String fragmentShaderSource) {
		int vertexShader = glCreateShader(GL_VERTEX_SHADER);
		glShaderSource(vertexShader, vertexShaderSource);
		glCompileShader(vertexShader);

		int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragmentShader, fragmentShaderSource);
		glCompileShader(fragmentShader);

		handler = glCreateProgram();
		glAttachShader(handler, vertexShader);
		glAttachShader(handler, fragmentShader);
		glLinkProgram(handler);

		glDetachShader(handler, vertexShader);
		glDetachShader(handler, fragmentShader);

		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);
	}

	public void use () {
		glUseProgram(handler);
	}

	public int getHandler () {
		return handler;
	}
}
