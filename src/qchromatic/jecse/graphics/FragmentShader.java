package qchromatic.jecse.graphics;

import org.lwjgl.opengl.GL20;

public final class FragmentShader extends Shader {
	public FragmentShader (String path) {
		super(path);

		shader = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
	}
}
