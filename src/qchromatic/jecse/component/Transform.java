package qchromatic.jecse.component;

import qchromatic.jecse.common.Vec3;
import qchromatic.jecse.core.Component;

public class Transform extends Component {
	private Vec3 _position;

	// region ctors
	public Transform (Transform other) {
		if (other == null) return;

		_position = new Vec3(other._position);
	}

	public Transform () {
		_position = new Vec3();
	}
	// endregion

	// region get-set
	public Transform position (Vec3 position) {
		_position = new Vec3(position);

		return this;
	}

	public Vec3 position () {
		return new Vec3(_position);
	}
	// endregion
}
