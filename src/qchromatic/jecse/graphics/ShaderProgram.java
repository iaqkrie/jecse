package qchromatic.jecse.graphics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL20.*;

public final class ShaderProgram {
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
		checkShaderCompilation(vertexShader);

		int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
		glShaderSource(fragmentShader, fragmentShaderSource);
		glCompileShader(fragmentShader);
		checkShaderCompilation(fragmentShader);

		handler = glCreateProgram();
		glAttachShader(handler, vertexShader);
		glAttachShader(handler, fragmentShader);
		glLinkProgram(handler);
		checkProgramLinking(handler);

		glDetachShader(handler, vertexShader);
		glDetachShader(handler, fragmentShader);

		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);
	}

	public static void clearActiveProgram() { glUseProgram(0); }

	public void use () { glUseProgram(handler); }

	public int getHandler () { return handler; }

	public void delete () { glDeleteProgram(handler); }

	private void checkShaderCompilation(int shader) {
		int compiled = glGetShaderi(shader, GL_COMPILE_STATUS);
		if (compiled == 0) {
			String log = glGetShaderInfoLog(shader);
			throw new RuntimeException("Shader compilation error: " + log);
		}
	}

	private void checkProgramLinking(int program) {
		int linked = glGetProgrami(program, GL_LINK_STATUS);
		if (linked == 0) {
			String log = glGetProgramInfoLog(program);
			throw new RuntimeException("Program linking error: " + log);
		}
	}
}
