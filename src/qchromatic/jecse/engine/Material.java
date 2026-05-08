package qchromatic.jecse.engine;

import qchromatic.jecse.common.Mat4;
import qchromatic.jecse.common.Vec2;
import qchromatic.jecse.common.Vec3;
import qchromatic.jecse.common.Vec4;
import qchromatic.jecse.graphics.Shader;
import qchromatic.jecse.graphics.Texture;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Material {
	private Shader _shader;
	private final Map<String, Object> _uniforms;
	private final Map<String, Texture> _textures;

	public Material () { this(Assets.basicShader(), Assets.debugTexture(), new Vec4(1f)); }
	public Material (Shader shader, Texture texture, Vec4 color) {
		if (shader == null)
			throw new IllegalArgumentException("Shader cannot be null");
		if (texture == null)
			throw new IllegalArgumentException("Texture cannot be null");
		if (color == null)
			throw new IllegalArgumentException("Color cannot be null");

		_shader = shader;
		_uniforms = new LinkedHashMap<>();
		_textures = new LinkedHashMap<>();

		_uniforms.put("u_color", new Vec4(color));
		_textures.put("u_texture", texture);
	}

	public void use () {
		_shader.use();

		int textureUnit = 0;
		for (Map.Entry<String, Texture> entry : _textures.entrySet()) {
			entry.getValue().bind(textureUnit);
			_shader.setUniform(entry.getKey(), textureUnit);
			textureUnit++;
		}

		for (Map.Entry<String, Object> entry : _uniforms.entrySet())
			applyUniform(entry.getKey(), entry.getValue());
	}

	public Shader shader () { return _shader; }

	public Texture texture () { return _textures.get("u_texture"); }

	public Vec4 color () {
		Object color = _uniforms.get("u_color");
		return color instanceof Vec4 vec ? new Vec4(vec) : new Vec4(1f);
	}

	public Map<String, Object> uniforms () { return Collections.unmodifiableMap(_uniforms); }

	public Map<String, Texture> textures () { return Collections.unmodifiableMap(_textures); }

	public Material shader (Shader shader) {
		if (shader == null)
			throw new IllegalArgumentException("Shader cannot be null");

		_shader = shader;
		return this;
	}

	public Material texture (Texture texture) {
		return setTexture("u_texture", texture);
	}

	public Material color (Vec4 color) {
		return set("u_color", color);
	}

	public Material set (String name, float value) {
		_uniforms.put(validateName(name), value);
		return this;
	}

	public Material set (String name, int value) {
		_uniforms.put(validateName(name), value);
		return this;
	}

	public Material set (String name, boolean value) {
		_uniforms.put(validateName(name), value);
		return this;
	}

	public Material set (String name, Vec2 value) {
		if (value == null)
			throw new IllegalArgumentException("Uniform value cannot be null");

		_uniforms.put(validateName(name), new Vec2(value));
		return this;
	}

	public Material set (String name, Vec3 value) {
		if (value == null)
			throw new IllegalArgumentException("Uniform value cannot be null");

		_uniforms.put(validateName(name), new Vec3(value));
		return this;
	}

	public Material set (String name, Vec4 value) {
		if (value == null)
			throw new IllegalArgumentException("Uniform value cannot be null");

		_uniforms.put(validateName(name), new Vec4(value));
		return this;
	}

	public Material set (String name, Mat4 value) {
		if (value == null)
			throw new IllegalArgumentException("Uniform value cannot be null");

		_uniforms.put(validateName(name), new Mat4(value.getMatrix()));
		return this;
	}

	public Material setTexture (String name, Texture texture) {
		if (texture == null)
			throw new IllegalArgumentException("Texture cannot be null");

		_textures.put(validateName(name), texture);
		return this;
	}

	public Shader getShader () { return _shader; }

	private void applyUniform (String name, Object value) {
		if (value instanceof Float uniform)
			_shader.setUniform(name, uniform);
		else if (value instanceof Integer uniform)
			_shader.setUniform(name, uniform);
		else if (value instanceof Boolean uniform)
			_shader.setUniform(name, uniform);
		else if (value instanceof Vec2 uniform)
			_shader.setUniform(name, uniform);
		else if (value instanceof Vec3 uniform)
			_shader.setUniform(name, uniform);
		else if (value instanceof Vec4 uniform)
			_shader.setUniform(name, uniform);
		else if (value instanceof Mat4 uniform)
			_shader.setUniform(name, uniform);
		else
			throw new IllegalStateException("Unsupported uniform type: " + value.getClass().getName());
	}

	private String validateName (String name) {
		if (name == null || name.isBlank())
			throw new IllegalArgumentException("Uniform name cannot be null or blank");

		return name;
	}
}
