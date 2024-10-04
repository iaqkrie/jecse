package qchromatic.jecse.graphics;

import org.lwjgl.opengl.GL20;

import java.io.FileReader;
import java.io.IOException;

public abstract class Shader {
	protected int shader;
	protected String source;

	public Shader (String path) {
		source = "";
		try (FileReader reader = new FileReader(path)) {
			int c;
			while ((c = reader.read()) != -1)
				source += (char)c;
		} catch (IOException e) {
			throw new IllegalStateException(e.getMessage());
		}
	}

	void compile () {
		GL20.glShaderSource(shader, source);
		GL20.glCompileShader(shader);
	}

	void attach (int program) { GL20.glAttachShader(program, shader); }
	void detach (int program) { GL20.glDetachShader(program, shader); }
}
