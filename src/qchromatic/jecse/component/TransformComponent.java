package qchromatic.jecse.component;

import qchromatic.jecse.common.Vec3;
import qchromatic.jecse.core.Component;

public class TransformComponent extends Component {
	private Vec3 _position;

	// region ctors
	public TransformComponent (TransformComponent other) {
		if (other == null) return;

		_position = new Vec3(other._position);
	}

	public TransformComponent () {
		_position = new Vec3();
	}
	// endregion

	public Vec3 position () { return new Vec3(_position); }
	public TransformComponent position (Vec3 position) {
		_position = new Vec3(position);
		return this;
	}
}
