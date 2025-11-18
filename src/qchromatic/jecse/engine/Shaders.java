package qchromatic.jecse.engine;

import qchromatic.jecse.graphics.Shader;

import java.nio.file.Path;

public final class Shaders {
	public static Shader basic () {
		return new Shader(
				Path.of("res/assets/shader/basic/basic.vert"),
				Path.of("res/assets/shader/basic/basic.frag")
		);
	}
}
