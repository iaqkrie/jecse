package qchromatic.jecse.graphics;

import org.lwjgl.opengl.GL20;

public final class VertexShader extends Shader {
	public VertexShader (String path) {
		super(path);

		shader = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
	}
}
