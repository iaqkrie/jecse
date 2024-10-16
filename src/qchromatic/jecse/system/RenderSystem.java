package qchromatic.jecse.system;

import qchromatic.jecse.component.Camera;
import qchromatic.jecse.component.SpriteRenderer;
import qchromatic.jecse.component.Transform;
import qchromatic.jecse.core.SceneManager;
import qchromatic.jecse.ecs.Entity;
import qchromatic.jecse.graphics.GraphicsEnviroment;
import qchromatic.jecse.math.Mat3f;
import qchromatic.jecse.math.Vec2f;

public class RenderSystem {
	public static void render () {
		if (SceneManager.getActiveScene() == null)
			throw new RuntimeException("No active scene!");

		if (!SceneManager.getActiveScene().hasEntityWithComponent(Camera.class))
			throw new RuntimeException("Camera not found!");

		if (!SceneManager.getActiveScene().hasEntityWithComponent(SpriteRenderer.class))
			return;

		Entity camera = SceneManager.getActiveScene().getEntitiesWithComponent(Camera.class)[0];
		Vec2f camPos = camera.getComponent(Transform.class).position;
		float camRot = camera.getComponent(Transform.class).rotation;
		float camZoom = camera.getComponent(Camera.class).zoom;

		Entity[] entities = SceneManager.getActiveScene().getEntitiesWithComponent(SpriteRenderer.class);
		for (Entity entity : entities) {
			Vec2f ePos = entity.getComponent(Transform.class).position;
			Vec2f eScale = entity.getComponent(Transform.class).scale;
			float eRot = entity.getComponent(Transform.class).rotation;

			Mat3f model = new Mat3f();
			model.setTranslation(ePos.x, ePos.y);
			model.setScale(eScale.x, eScale.y);
			model.setRotation(eRot);

			Mat3f view = new Mat3f();
			view.setTranslation(-camPos.x, -camPos.y);
			view.setScale(camZoom, camZoom);
			view.setRotation(-camRot);

			Mat3f projection = GraphicsEnviroment.getProjectionMatrix();

			entity.getComponent(SpriteRenderer.class).sprite.draw(model, view, projection);
		}
	}
}
