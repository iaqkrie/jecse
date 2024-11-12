package qchromatic.jecse.component;

import qchromatic.jecse.core.Component;

public class CameraComponent extends Component {
	public float zoom = 1f;

	public CameraComponent () {
		dependencies.add(Transform.class);
	}
}
