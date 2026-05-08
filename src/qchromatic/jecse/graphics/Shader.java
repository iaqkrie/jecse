package qchromatic.jecse.graphics;

import qchromatic.jecse.common.Mat4;
import qchromatic.jecse.common.Vec2;
import qchromatic.jecse.common.Vec3;
import qchromatic.jecse.common.Vec4;
import qchromatic.jecse.core.Disposable;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL20.*;

public final class Shader implements Disposable {
	private int _handler;
	private final Map<String, Integer> _uniformLocations;

	public Shader (Path vertexShaderPath, Path fragmentShaderPath) {
		_uniformLocations = new HashMap<>();

		try {
			String vss = new String(Files.readAllBytes(vertexShaderPath));
			String fss = new String(Files.readAllBytes(fragmentShaderPath));

			createShaders(vss, fss);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	public Shader (byte[] vertexShaderSource, byte[] fragmentShaderSource) {
		this(new String(vertexShaderSource), new String(fragmentShaderSource));
	}
	public Shader (String vertexShaderSource, String fragmentShaderSource) {
		_uniformLocations = new HashMap<>();
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

	public void setUniform (String name, Vec4 value) {
		glUniform4f(getUniformLocation(name), value.x, value.y, value.z, value.w);
	}
	public void setUniform (String name, Vec3 value) {
		glUniform3f(getUniformLocation(name), value.x, value.y, value.z);
	}
	public void setUniform (String name, Vec2 value) {
		glUniform2f(getUniformLocation(name), value.x, value.y);
	}
	public void setUniform (String name, float value) {
		glUniform1f(getUniformLocation(name), value);
	}
	public void setUniform (String name, int value) {
		glUniform1i(getUniformLocation(name), value);
	}
	public void setUniform (String name, boolean value) {
		glUniform1i(getUniformLocation(name), value ? 1 : 0);
	}
	public void setUniform (String name, Mat4 value) {
		glUniformMatrix4fv(getUniformLocation(name), false, value.getMatrix());
	}

	public void use () { glUseProgram(_handler); }
	public static void stopUsing() { glUseProgram(0); }

	public int getUniformLocation (String name) {
		return _uniformLocations.computeIfAbsent(name, uniform -> glGetUniformLocation(_handler, uniform));
	}

	public int handler () { return _handler; }

	@Override
    public void destroy () {
		if (_handler == 0) return;

		glDeleteProgram(_handler);
		_handler = 0;
		_uniformLocations.clear();
	}
}
