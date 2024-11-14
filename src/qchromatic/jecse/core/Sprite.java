package qchromatic.jecse.core;

import qchromatic.jecse.graphics.GraphicsEnviroment;
import qchromatic.jecse.graphics.Texture;
import qchromatic.jecse.math.Mat3f;

public class Sprite {
	private Color _color;
	private Texture _texture;

	public Sprite (Texture texture) {
		_color = Color.WHITE;

		TextureManager.load(texture);
		_texture = texture;
	}

	public void draw (Mat3f model, Mat3f view, Mat3f projection) {
		GraphicsEnviroment.render(model, view, projection, _color, _texture);
	}

	public void setColor (Color color) {
		_color = new Color(color);
	}

	public Texture getTexture () { return _texture; }
}
