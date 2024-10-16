package qchromatic.jecse.component;

import qchromatic.jecse.ecs.Component;

public class Camera extends Component {
	public float zoom = 1f;

	public Camera () {
		dependencies.add(Transform.class);
	}
}
