package qchromatic.jecse.entity;

import qchromatic.jecse.component.CameraComponent;
import qchromatic.jecse.component.Transform;
import qchromatic.jecse.core.Entity;
import qchromatic.jecse.math.Mat3f;
import qchromatic.jecse.math.Vec2f;

public class Camera extends Entity {
	public Camera () {
		super();

		addComponent(new CameraComponent());
	}
}
