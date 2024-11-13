package qchromatic.jecse.system;

import qchromatic.jecse.component.CameraComponent;
import qchromatic.jecse.component.SpriteRenderer;
import qchromatic.jecse.component.Transform;
import qchromatic.jecse.core.SceneManager;
import qchromatic.jecse.core.Entity;
import qchromatic.jecse.graphics.GraphicsEnviroment;
import qchromatic.jecse.math.Mat3f;
import qchromatic.jecse.math.Vec2f;

public class RenderSystem {
	public static void render () {
		if (SceneManager.getActiveScene() == null)
			throw new RuntimeException("No active scene!");

		if (!SceneManager.getActiveScene().hasEntityWithComponent(CameraComponent.class))
			throw new RuntimeException("Camera not found!");

		if (!SceneManager.getActiveScene().hasEntityWithComponent(SpriteRenderer.class))
			return;

		Entity camera = SceneManager.getActiveScene().getEntitiesWithComponent(CameraComponent.class)[0];
		Vec2f camPos = camera.getComponent(Transform.class).position();
		float camRot = camera.getComponent(Transform.class).rotation();
		float camZoom = camera.getComponent(CameraComponent.class).zoom();

		Entity[] entities = SceneManager.getActiveScene().getEntitiesWithComponent(SpriteRenderer.class);
		for (Entity entity : entities) {
			Vec2f ePos = entity.getComponent(Transform.class).position();
			Vec2f eScale = entity.getComponent(Transform.class).scale();
			float eRot = entity.getComponent(Transform.class).rotation();

			float ppu = entity.getComponent(SpriteRenderer.class).pixelsPerUnit();
			Vec2f ppuScale = new Vec2f();
			ppuScale.x = entity.getComponent(SpriteRenderer.class).sprite().getTexture().getSize().x / ppu;
			ppuScale.y = entity.getComponent(SpriteRenderer.class).sprite().getTexture().getSize().y / ppu;

			Mat3f s;
			Mat3f r;
			Mat3f t;

			Mat3f model = new Mat3f();
			s = new Mat3f(new float[] {
					eScale.x * ppuScale.x, 0,                     0,
					0,                     eScale.y * ppuScale.y, 0,
					0,                     0,                     1
			});
			r = new Mat3f(new float[] {
					(float) Math.cos(Math.toRadians(eRot)), (float) -Math.sin(Math.toRadians(eRot)), 0,
					(float) Math.sin(Math.toRadians(eRot)), (float) Math.cos(Math.toRadians(eRot)),  0,
					0,                                      0,                                       1
			});
			t = new Mat3f(new float[] {
					1, 0, ePos.x,
					0, 1, ePos.y,
					0, 0, 1
			});
			model.mul(t).mul(r).mul(s);

			Mat3f view = new Mat3f();
			s = new Mat3f(new float[] {
					camZoom, 0,       0,
					0,       camZoom, 0,
					0,       0,       1
			});
			r = new Mat3f(new float[] {
					(float) Math.cos(Math.toRadians(-camRot)), (float) -Math.sin(Math.toRadians(-camRot)), 0,
					(float) Math.sin(Math.toRadians(-camRot)), (float) Math.cos(Math.toRadians(-camRot)),  0,
					0,                                         0,                                          1
			});
			t = new Mat3f(new float[] {
					1, 0, -camPos.x,
					0, 1, -camPos.y,
					0, 0, 1
			});
			view.mul(t).mul(r).mul(s);

			Mat3f projection = GraphicsEnviroment.getProjectionMatrix();

			entity.getComponent(SpriteRenderer.class).sprite().draw(model, view, projection);
		}
	}
}
