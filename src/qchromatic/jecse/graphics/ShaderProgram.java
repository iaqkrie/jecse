package qchromatic.jecse.graphics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL20.*;

public final class ShaderProgram {
	private int _handler;

	public ShaderProgram (Path vertexShaderPath, Path fragmentShaderPath) {
		try {
			String vss = new String(Files.readAllBytes(vertexShaderPath));
			String fss = new String(Files.readAllBytes(fragmentShaderPath));

			createShaders(vss, fss);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public ShaderProgram (byte[] vertexShaderSource, byte[] fragmentShaderSource) {
		this(new String(vertexShaderSource), new String(fragmentShaderSource));
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

		_handler = glCreateProgram();
		glAttachShader(_handler, vertexShader);
		glAttachShader(_handler, fragmentShader);
		glLinkProgram(_handler);
		checkProgramLinking(_handler);

		glDetachShader(_handler, vertexShader);
		glDetachShader(_handler, fragmentShader);

		glDeleteShader(vertexShader);
		glDeleteShader(fragmentShader);
	}

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

	public static void stopUsingProgram () { glUseProgram(0); }

	public int getUniformLocation (String name) {
		return glGetUniformLocation(_handler, name);
	}

	public void setUniform (int location, float value) {
		glUniform1f(location, value);
	}

	public void getLocationAndSetUniform (String name, float value) {
		setUniform(getUniformLocation(name), value);
	}

	public void use () { glUseProgram(_handler); }

	public int getHandler () { return _handler; }

	public void delete () { glDeleteProgram(_handler); }
}
