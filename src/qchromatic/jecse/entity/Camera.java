package qchromatic.jecse.entity;

import qchromatic.jecse.component.CameraComponent;
import qchromatic.jecse.core.Entity;

public class Camera extends Entity {
	public Camera () {
		super();

		addComponent(new CameraComponent());
	}
}
