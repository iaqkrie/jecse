package qchromatic.jecse.system;

import qchromatic.jecse.component.MeshRenderer;
import qchromatic.jecse.core.Entity;
import qchromatic.jecse.core.System;
import qchromatic.jecse.engine.Mesh;

import java.util.List;

public class RenderSystem extends System {
	private Entity _camera;
	private List<Entity> _entities;

	@Override
	public void loop (float dtime) {
		for (Entity entity : _entities)
			renderMesh(entity.getComponent(MeshRenderer.class).mesh());
	}

	private void renderMesh (Mesh mesh) {

	}
}
