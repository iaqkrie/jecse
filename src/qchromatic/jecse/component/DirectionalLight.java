package qchromatic.jecse.component;

import qchromatic.jecse.common.Vec3;
import qchromatic.jecse.common.Vec4;
import qchromatic.jecse.core.Component;

public class DirectionalLight extends Component {
	private Vec3 _direction;
	private Vec4 _color;
	private float _intensity;
	private boolean _enabled;

	public DirectionalLight () {
		_direction = new Vec3(-0.3f, -1f, -0.4f).normalized();
		_color = new Vec4(1f, 1f, 1f, 1f);
		_intensity = 1f;
		_enabled = true;
	}

	public Vec3 direction () { return new Vec3(_direction); }
	public DirectionalLight direction (Vec3 direction) {
		if (direction == null)
			throw new IllegalArgumentException("Light direction cannot be null");

		_direction = direction.normalized();
		return this;
	}
	public DirectionalLight direction (float x, float y, float z) {
		return direction(new Vec3(x, y, z));
	}

	public Vec4 color () { return new Vec4(_color); }
	public DirectionalLight color (Vec4 color) {
		if (color == null)
			throw new IllegalArgumentException("Light color cannot be null");

		_color = new Vec4(color);
		return this;
	}

	public float intensity () { return _intensity; }
	public DirectionalLight intensity (float intensity) {
		_intensity = Math.max(0f, intensity);
		return this;
	}

	public boolean enabled () { return _enabled; }
	public DirectionalLight enabled (boolean enabled) {
		_enabled = enabled;
		return this;
	}
}
