package qchromatic.jecse.engine;

import qchromatic.jecse.graphics.Shader;
import qchromatic.jecse.graphics.Texture;

import java.nio.file.Path;

public final class Assets {
	private Assets () { }

	private static AssetManager _current = new AssetManager();

	public static AssetManager current () { return _current; }

	public static void use (AssetManager assets) {
		if (assets == null)
			throw new IllegalArgumentException("Asset manager cannot be null");

		_current = assets;
	}

	public static Shader basicShader () {
		return _current.basicShader();
	}

	public static Texture debugTexture () {
		return _current.debugTexture();
	}

	public static Texture texture (String key, Path path) {
		return _current.texture(key, path);
	}
}
