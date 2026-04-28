package qchromatic.jecse.system;

import qchromatic.jecse.component.Camera;
import qchromatic.jecse.component.MeshRenderer;
import qchromatic.jecse.component.Transform;
import qchromatic.jecse.core.Entity;
import qchromatic.jecse.core.System;
import qchromatic.jecse.engine.Material;
import qchromatic.jecse.engine.Mesh;
import qchromatic.jecse.graphics.MeshGpuResource;
import qchromatic.jecse.graphics.Shader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL30.*;

public class RenderSystem extends System {
	private final Map<Mesh, MeshGpuResource> _meshResources = new HashMap<>();

	private List<Entity> _entities;
	private Entity _cameraEntity;

	@Override
	public void init () {
		updateEntitiesList();

		_cameraEntity = scene.getEntitiesWithComponents(
				Transform.class,
				Camera.class)[0];
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

	@Override
	public void destroy() {
		for (MeshGpuResource resource : _meshResources.values())
			resource.destroy();

		_meshResources.clear();
	}

	private void updateEntitiesList () {
		_entities = List.of(scene.getEntitiesWithComponents(
				Transform.class,
				MeshRenderer.class));
	}

	private void renderEntity (Entity entity) {
		MeshRenderer meshRenderer = entity.getComponent(MeshRenderer.class);
		Transform transform = entity.getComponent(Transform.class);

		if (meshRenderer == null || transform == null) return;

		Mesh mesh = meshRenderer.mesh();
		Material material = meshRenderer.material();

		if (mesh == null || material == null) return;

		MeshGpuResource resource = _meshResources.get(mesh);

		if (resource == null) {
			resource = createMeshGpuResource(mesh);
			_meshResources.put(mesh, resource);
		}

		material.use();

		Camera camera = _cameraEntity.getComponent(Camera.class);
		Shader shader = material.getShader();

		shader.setUniform("u_model", transform.getModelMatrix().transposed());
		shader.setUniform("u_view", camera.getViewMatrix().transposed());
		shader.setUniform("u_projection", camera.getProjectionMatrix().transposed());

		glBindVertexArray(resource.vaoId());
		glDrawElements(GL_TRIANGLES, mesh.triangles().length, GL_UNSIGNED_INT, 0);
		glBindVertexArray(0);
	}

	private MeshGpuResource createMeshGpuResource (Mesh mesh) {
		int vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);

		int vboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, mesh.vertices(), GL_STATIC_DRAW);

		glVertexAttribPointer(0, 3, GL_FLOAT, false, 5 * Float.BYTES, 0);
		glEnableVertexAttribArray(0);

		glVertexAttribPointer(1, 2, GL_FLOAT, false, 5 * Float.BYTES, 3 * Float.BYTES);
		glEnableVertexAttribArray(1);

		int eboId = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, mesh.triangles(), GL_STATIC_DRAW);

		glBindVertexArray(0);

		return new MeshGpuResource(vaoId, vboId, eboId);
	}
}
