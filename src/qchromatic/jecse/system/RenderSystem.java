package qchromatic.jecse.system;

import qchromatic.jecse.component.MeshComponent;
import qchromatic.jecse.core.Entity;
import qchromatic.jecse.core.System;
import qchromatic.jecse.engine.Mesh;
import qchromatic.jecse.engine.SceneManager;
import qchromatic.jecse.graphics.Shader;

import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class RenderSystem extends System {
	private List<Entity> _entities;

	@Override
	public void init () {
		updateEntitiesList();
	}

	@Override
	public void loop (float dtime) {
		for (Entity entity : _entities)
			renderEntity(entity);
	}

	@Override
	public void update () {
		updateEntitiesList();
	}

	private void updateEntitiesList () {
		_entities = List.of(scene.getEntitiesWithComponent(MeshComponent.class));
	}

	private void renderEntity (Entity entity) {
		MeshComponent meshComponent = entity.getComponent(MeshComponent.class);
		Mesh mesh = meshComponent.mesh();
		Shader shader = meshComponent.shader();

		shader.use();

		int vaoId = createVAO(mesh);

		glBindVertexArray(vaoId);
		glDrawElements(GL_TRIANGLES, mesh.triangles().length, GL_UNSIGNED_INT, 0);
		glBindVertexArray(0);
	}

	private int createVAO (Mesh mesh) {
		int vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);

		int vboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, mesh.vertices(), GL_STATIC_DRAW);

		glVertexAttribPointer(0, 3, GL_FLOAT, false, 3 * Float.BYTES, 0);
		glEnableVertexAttribArray(0);

		int eboId = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, mesh.triangles(), GL_STATIC_DRAW);

		glBindVertexArray(0);

		return vaoId;
	}
}
