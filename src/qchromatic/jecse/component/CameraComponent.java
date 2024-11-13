package qchromatic.jecse.component;

import qchromatic.jecse.core.Component;

public class CameraComponent extends Component {
	private float _zoom = 1f;

	public CameraComponent () {
		dependencies.add(Transform.class);
	}

	public float zoom () { return _zoom; }

	public CameraComponent zoom (float zoom) {
		_zoom = zoom;
		return this;
	}
}
