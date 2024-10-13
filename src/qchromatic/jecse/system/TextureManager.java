package qchromatic.jecse.system;

import qchromatic.jecse.graphics.Texture;

import java.util.ArrayList;
import java.util.List;

public class TextureManager {
	private static List<Texture> _textures;

	public static void init () {
		_textures = new ArrayList<>();

		load(new Texture("res/texture/debug.png")); // TODO System check (unix or windows)
	}

	public static int load (Texture texture) {
		if (texture == null)
			throw new RuntimeException("Texture is null!");

		int textureID = _textures.size();

		texture.createOnGPU();
		_textures.add(texture);

		return textureID;
	}

	public static Texture get (int id) {
		if (_textures.size() <= id)
			throw new RuntimeException("No such texture id!");

		return _textures.get(id);
	}

	public static void unloadTextures () {
		for (Texture t : _textures)
			t.deleteFromGPU();
	}
}
