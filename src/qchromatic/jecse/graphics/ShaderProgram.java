package qchromatic.jecse.graphics;

import org.lwjgl.opengl.GL20;

import java.util.ArrayList;
import java.util.List;

public final class ShaderProgram {
	private final int _program;
	private final List<Shader> _shaders;

	public ShaderProgram () {
		_program = GL20.glCreateProgram();

		_shaders = new ArrayList<>();
	}

	public void addShader (Shader shader) { _shaders.add(shader); }

	public void link () {
		for (Shader shader : _shaders) {
			shader.compile();
			shader.attach(_program);
		}

		GL20.glLinkProgram(_program);

		for (Shader shader : _shaders) {
			shader.detach(_program);
		}
	}

	public void use () { GL20.glUseProgram(_program); }
	public void disable () { GL20.glUseProgram(0); }
}
