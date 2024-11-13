package qchromatic.jecse.component;

import qchromatic.jecse.core.Component;
import qchromatic.jecse.math.Vec2f;

public class Transform extends Component {
	private Vec2f _position;
	private float _rotation;
	private Vec2f _scale;

	public Transform () { this(new Vec2f(), 0f, new Vec2f(1f)); }
	public Transform (Vec2f position, float rotation, Vec2f scale) {
		_position = new Vec2f(position);
		_rotation = rotation;
		_scale = new Vec2f(scale);
	}

	public Vec2f position () { return new Vec2f(_position); }
	public float rotation () { return _rotation; }
	public Vec2f scale () { return new Vec2f(_scale); }

	public Transform position (float x, float y) { return position(new Vec2f(x, y)); }
	public Transform position (Vec2f position) {
		_position = position;
		return this;
	}

	public Transform rotation (float rotation) {
		_rotation = rotation;
		return this;
	}

	public Transform scale (float x, float y) { return scale(new Vec2f(x, y)); }
	public Transform scale (Vec2f scale) {
		_scale = scale;
		return this;
	}
}
