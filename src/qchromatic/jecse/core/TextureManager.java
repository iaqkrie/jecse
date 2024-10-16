package qchromatic.jecse.core;

import qchromatic.jecse.graphics.Texture;
import qchromatic.jecse.math.Vec2;

import java.util.ArrayList;
import java.util.List;

public final class TextureManager {
	private static List<Texture> _textures;

	public static void init () {
		_textures = new ArrayList<>();

		Texture debugTexture = new Texture(new Vec2(4, 4));
		float base = 1f / 3f;
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 4; j++) {
				float r = base * i;
				float g = base * (3 - j);
				float b = base * (3 - Math.max(i, (3 - j)));
				float a = 1f;

				debugTexture.setPixel(i, j, r, g, b, a);
			}
		}

		load(debugTexture);
	}

	public static int load (Texture texture) {
		if (texture == null)
			throw new RuntimeException("Texture is null!");

		if (_textures.contains(texture))
			return _textures.indexOf(texture);

		int textureID = _textures.size();

		texture.createOnGPU();
		_textures.add(texture);

		return textureID;
	}

	public static Texture getTexture (int id) {
		if (_textures.size() <= id)
			throw new RuntimeException("No such texture id!");

		return _textures.get(id);
	}

	public static void unloadTextures () {
		for (Texture texture : _textures)
			texture.deleteFromGPU();
	}
}
