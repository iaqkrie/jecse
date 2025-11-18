package qchromatic.jecse.engine;

import qchromatic.jecse.common.Vec4;
import qchromatic.jecse.graphics.Shader;
import qchromatic.jecse.graphics.Texture;

public class Material {
	private Shader _shader;
	private Texture _texture;
	private Vec4 _color;

	public Material () { this(Shaders.basic(), Texture.debugTexture(), new Vec4(1f)); }
	public Material (Shader shader, Texture texture, Vec4 color) {
		_shader = shader;
		_texture = texture;
		_color = color;
	}

	public void use () {
		_shader.use();

		_shader.setUniform("u_color", _color);
		_texture.bind(0);
		_shader.setUniform("u_texture", 0);
	}

	public Shader getShader () { return _shader; }
}
