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

	public static Mat3f getViewMatrix (Entity camera) {
		if (!camera.containsComponent(CameraComponent.class))
			throw new RuntimeException("Entity is not camera");

		Transform transform = camera.getComponent(Transform.class);
		Vec2f pos = transform.position();
		float rot = transform.rotation();
		float zoom = camera.getComponent(CameraComponent.class).zoom();

		Mat3f view = new Mat3f();

		Mat3f s = new Mat3f(new float[] {
				zoom, 0,    0,
				0,    zoom, 0,
				0,    0,    1
		});

		Mat3f r = new Mat3f(new float[] {
				(float) Math.cos(Math.toRadians(-rot)), (float) -Math.sin(Math.toRadians(-rot)), 0,
				(float) Math.sin(Math.toRadians(-rot)), (float) Math.cos(Math.toRadians(-rot)),  0,
				0,                                      0,                                       1
		});

		Mat3f t = new Mat3f(new float[] {
				1, 0, -pos.x,
				0, 1, -pos.y,
				0, 0, 1
		});

		return view.mul(t).mul(r).mul(s);
	}
}
