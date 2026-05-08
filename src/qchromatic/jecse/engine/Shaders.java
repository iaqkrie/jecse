package qchromatic.jecse.engine;

import qchromatic.jecse.graphics.Shader;

public final class Shaders {
	private Shaders () { }

	public static Shader basic () {
		return Assets.basicShader();
	}
}
