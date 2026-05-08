package qchromatic.jecse.system;

import qchromatic.jecse.common.Vec3;
import qchromatic.jecse.common.Vec4;
import qchromatic.jecse.component.Camera;
import qchromatic.jecse.component.DirectionalLight;
import qchromatic.jecse.component.MeshRenderer;
import qchromatic.jecse.component.Transform;
import qchromatic.jecse.core.Entity;
import qchromatic.jecse.core.EntityQuery;
import qchromatic.jecse.core.System;
import qchromatic.jecse.engine.EngineContext;
import qchromatic.jecse.engine.Material;
import qchromatic.jecse.engine.Mesh;
import qchromatic.jecse.graphics.MeshGpuResource;
import qchromatic.jecse.graphics.Shader;

import java.util.*;

import static org.lwjgl.opengl.GL11.glGetError;
import static org.lwjgl.opengl.GL11.GL_NO_ERROR;
import static org.lwjgl.opengl.GL30.*;

public class RenderSystem extends System {
	private final Map<Mesh, MeshGpuResource> _meshResources;
	private final List<RenderCommand> _renderQueue;

	private EntityQuery _renderables;
	private EntityQuery _cameras;
	private EntityQuery _lights;
	private Vec4 _ambientColor;

	public RenderSystem () {
		super(1000);
		_meshResources = new HashMap<>();
		_renderQueue = new ArrayList<>();
		_ambientColor = new Vec4(0.2f, 0.2f, 0.2f, 1f);
	}

	public Vec4 ambientColor () { return new Vec4(_ambientColor); }

	public RenderSystem ambientColor (Vec4 ambientColor) {
		if (ambientColor == null)
			throw new IllegalArgumentException("Ambient color cannot be null");

		_ambientColor = new Vec4(ambientColor);
		return this;
	}

	@Override
	public void init () {
		_renderables = scene.query(Transform.class, MeshRenderer.class);
		_cameras = scene.query(Transform.class, Camera.class);
		_lights = scene.query(DirectionalLight.class);
	}

	@Override
	public void loop (float dtime) {
		Entity cameraEntity = selectCamera();
		if (cameraEntity == null) return;

		Camera camera = cameraEntity.getComponent(Camera.class);
		LightState light = selectLight();

		buildRenderQueue(camera);
		sortRenderQueue();

		for (RenderCommand command : _renderQueue)
			renderCommand(command, camera, light);

		checkOpenGLErrors();
	}

	@Override
	public void destroy() {
		for (MeshGpuResource resource : _meshResources.values())
			resource.destroy();

		_meshResources.clear();
		_renderQueue.clear();
	}

	private Entity selectCamera () {
		Entity selected = null;
		int selectedPriority = Integer.MIN_VALUE;

		for (Entity entity : _cameras) {
			Camera camera = entity.getComponent(Camera.class);
			if (camera == null || !camera.enabled()) continue;

			if (selected == null || camera.priority() > selectedPriority) {
				selected = entity;
				selectedPriority = camera.priority();
			}
		}

		return selected;
	}

	private LightState selectLight () {
		for (Entity entity : _lights) {
			DirectionalLight light = entity.getComponent(DirectionalLight.class);
			if (light != null && light.enabled())
				return new LightState(light.direction(), light.color().multiplied(light.intensity()), true);
		}

		return new LightState(new Vec3(0f, -1f, 0f), new Vec4(1f), false);
	}

	private void buildRenderQueue (Camera camera) {
		_renderQueue.clear();

		for (Entity entity : _renderables) {
			MeshRenderer meshRenderer = entity.getComponent(MeshRenderer.class);
			Transform transform = entity.getComponent(Transform.class);

			if (meshRenderer == null || transform == null || !meshRenderer.enabled()) continue;
			if ((camera.cullingMask() & meshRenderer.layer()) == 0) continue;

			Mesh mesh = meshRenderer.mesh();
			Material material = meshRenderer.material();

			if (mesh == null || material == null) continue;
			if (!isVisible(camera, transform, meshRenderer)) continue;

			_renderQueue.add(new RenderCommand(mesh, material, transform, meshRenderer.renderQueue()));
		}
	}

	private void sortRenderQueue () {
		_renderQueue.sort(Comparator
				.comparingInt((RenderCommand command) -> command.renderQueue)
				.thenComparingInt(command -> command.material.shader().handler())
				.thenComparingInt(command -> command.material.texture().id())
				.thenComparingInt(command -> java.lang.System.identityHashCode(command.mesh)));
	}

	private boolean isVisible (Camera camera, Transform transform, MeshRenderer renderer) {
		float radius = renderer.boundsRadius();
		if (radius <= 0f) return true;

		Vec3 cameraSpace = camera.getViewMatrix().transformPoint(transform.worldPosition());
		float depth = -cameraSpace.z;

		if (depth + radius < camera.near() || depth - radius > camera.far())
			return false;

		if (camera.ortho()) {
			float halfHeight = camera.orthoSize();
			float halfWidth = halfHeight * camera.aspectRatio();
			return Math.abs(cameraSpace.x) <= halfWidth + radius
					&& Math.abs(cameraSpace.y) <= halfHeight + radius;
		}

		if (depth <= 0f) return false;

		float halfHeight = (float) Math.tan(Math.toRadians(camera.fov()) * 0.5f) * depth;
		float halfWidth = halfHeight * camera.aspectRatio();

		return Math.abs(cameraSpace.x) <= halfWidth + radius
				&& Math.abs(cameraSpace.y) <= halfHeight + radius;
	}

	private void renderCommand (RenderCommand command, Camera camera, LightState light) {
		MeshGpuResource resource = getMeshGpuResource(command.mesh);
		Material material = command.material;

		material.use();

		Shader shader = material.shader();
		shader.setUniform("u_model", command.transform.getModelMatrix().transposed());
		shader.setUniform("u_view", camera.getViewMatrix().transposed());
		shader.setUniform("u_projection", camera.getProjectionMatrix().transposed());
		shader.setUniform("u_lightDirection", light.direction);
		shader.setUniform("u_lightColor", light.color);
		shader.setUniform("u_ambientColor", _ambientColor);
		shader.setUniform("u_lit", light.enabled);

		glBindVertexArray(resource.vaoId());
		glDrawElements(GL_TRIANGLES, resource.indexCount(), GL_UNSIGNED_INT, 0);
		glBindVertexArray(0);
	}

	private MeshGpuResource getMeshGpuResource (Mesh mesh) {
		MeshGpuResource resource = _meshResources.get(mesh);
		if (resource != null && resource.meshVersion() == mesh.version())
			return resource;

		if (resource != null)
			resource.destroy();

		resource = createMeshGpuResource(mesh);
		_meshResources.put(mesh, resource);
		return resource;
	}

	private MeshGpuResource createMeshGpuResource (Mesh mesh) {
		int vaoId = glGenVertexArrays();
		glBindVertexArray(vaoId);

		int vboId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vboId);
		glBufferData(GL_ARRAY_BUFFER, mesh.vertices(), GL_STATIC_DRAW);

		int strideBytes = mesh.vertexStride() * Float.BYTES;
		glVertexAttribPointer(0, 3, GL_FLOAT, false, strideBytes, 0);
		glEnableVertexAttribArray(0);

		if (mesh.vertexStride() >= 5) {
			glVertexAttribPointer(1, 2, GL_FLOAT, false, strideBytes, 3L * Float.BYTES);
			glEnableVertexAttribArray(1);
		} else {
			glDisableVertexAttribArray(1);
			glVertexAttrib2f(1, 0f, 0f);
		}

		if (mesh.vertexStride() >= 8) {
			glVertexAttribPointer(2, 3, GL_FLOAT, false, strideBytes, 5L * Float.BYTES);
			glEnableVertexAttribArray(2);
		} else {
			glDisableVertexAttribArray(2);
			glVertexAttrib3f(2, 0f, 0f, 1f);
		}

		int eboId = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboId);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, mesh.triangles(), GL_STATIC_DRAW);

		glBindVertexArray(0);

		return new MeshGpuResource(vaoId, vboId, eboId, mesh.version(), mesh.indexCount());
	}

	private void checkOpenGLErrors () {
		EngineContext context = EngineContext.currentOrNull();
		if (context == null || !context.config().debugOpenGLErrors()) return;

		int error = glGetError();
		if (error != GL_NO_ERROR)
			throw new RuntimeException("OpenGL error: " + error);
	}

	private static final class RenderCommand {
		private final Mesh mesh;
		private final Material material;
		private final Transform transform;
		private final int renderQueue;

		private RenderCommand (Mesh mesh, Material material, Transform transform, int renderQueue) {
			this.mesh = mesh;
			this.material = material;
			this.transform = transform;
			this.renderQueue = renderQueue;
		}
	}

	private static final class LightState {
		private final Vec3 direction;
		private final Vec4 color;
		private final boolean enabled;

		private LightState (Vec3 direction, Vec4 color, boolean enabled) {
			this.direction = direction;
			this.color = color;
			this.enabled = enabled;
		}
	}
}
