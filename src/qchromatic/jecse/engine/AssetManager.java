package qchromatic.jecse.engine;

import qchromatic.jecse.graphics.Shader;
import qchromatic.jecse.graphics.Texture;

import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class AssetManager {
	private final Map<String, Shader> _shaders;
	private final Map<String, Texture> _textures;
	private Texture _debugTexture;

	public AssetManager () {
		_shaders = new LinkedHashMap<>();
		_textures = new LinkedHashMap<>();
	}

	public Shader shader (String key, Path vertexShaderPath, Path fragmentShaderPath) {
		if (key == null || key.isBlank())
			throw new IllegalArgumentException("Shader key cannot be null or blank");

		return _shaders.computeIfAbsent(key, ignored -> new Shader(vertexShaderPath, fragmentShaderPath));
	}

	public Shader basicShader () {
		return shader(
				"shader/basic",
				Path.of("res/assets/shader/basic/basic.vert"),
				Path.of("res/assets/shader/basic/basic.frag")
		);
	}

	public Texture debugTexture () {
		if (_debugTexture == null)
			_debugTexture = Texture.debugTexture();

		return _debugTexture;
	}

	public Texture texture (String key, Path path) {
		if (key == null || key.isBlank())
			throw new IllegalArgumentException("Texture key cannot be null or blank");

		return _textures.computeIfAbsent(key, ignored -> Texture.load(path));
	}

	public void destroy () {
		for (Shader shader : _shaders.values())
			shader.destroy();

		_shaders.clear();

		for (Texture texture : _textures.values())
			texture.destroy();

		_textures.clear();

		if (_debugTexture != null) {
			_debugTexture.destroy();
			_debugTexture = null;
		}
	}
}
