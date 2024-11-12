package qchromatic.jecse.component;

import qchromatic.jecse.core.Component;
import qchromatic.jecse.math.Vec2f;

public class Transform extends Component {
	public Vec2f position;
	public float rotation;
	public Vec2f scale;

	public Transform () { this(new Vec2f(), 0f, new Vec2f(1f)); }
	public Transform (Vec2f position, float rotation, Vec2f scale) {
		this.position = new Vec2f(position);
		this.rotation = rotation;
		this.scale = new Vec2f(scale);
	}
}
