package qchromatic.jecse.system;

import org.lwjgl.BufferUtils;
import qchromatic.jecse.component.Camera;
import qchromatic.jecse.component.Transform;
import qchromatic.jecse.core.Entity;
import qchromatic.jecse.core.EntityQuery;
import qchromatic.jecse.core.System;
import qchromatic.jecse.engine.DebugRenderer;
import qchromatic.jecse.graphics.Shader;

import java.nio.FloatBuffer;
import java.util.List;

import static org.lwjgl.opengl.GL30.*;

public class DebugRenderSystem extends System {
	private Shader _shader;
	private EntityQuery _cameras;
	private int _vao;
	private int _vbo;

	public DebugRenderSystem () {
		super(1100);
	}

	@Override
	public void init () {
		_cameras = scene.query(Transform.class, Camera.class);
		_shader = new Shader(debugVertexShader(), debugFragmentShader());

		_vao = glGenVertexArrays();
		_vbo = glGenBuffers();

		glBindVertexArray(_vao);
		glBindBuffer(GL_ARRAY_BUFFER, _vbo);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 7 * Float.BYTES, 0);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(1, 4, GL_FLOAT, false, 7 * Float.BYTES, 3L * Float.BYTES);
		glEnableVertexAttribArray(1);
		glBindVertexArray(0);
	}

	@Override
	public void loop (float dtime) {
		Entity cameraEntity = selectCamera();
		if (cameraEntity == null) return;

		List<DebugRenderer.Line> lines = DebugRenderer.consumeLines();
		if (lines.isEmpty()) return;

		Camera camera = cameraEntity.getComponent(Camera.class);
		_shader.use();
		_shader.setUniform("u_view", camera.getViewMatrix().transposed());
		_shader.setUniform("u_projection", camera.getProjectionMatrix().transposed());

		FloatBuffer vertices = BufferUtils.createFloatBuffer(lines.size() * 2 * 7);
		for (DebugRenderer.Line line : lines) {
			putVertex(vertices, line.start.x, line.start.y, line.start.z, line.color.x, line.color.y, line.color.z, line.color.w);
			putVertex(vertices, line.end.x, line.end.y, line.end.z, line.color.x, line.color.y, line.color.z, line.color.w);
		}
		vertices.flip();

		glBindVertexArray(_vao);
		glBindBuffer(GL_ARRAY_BUFFER, _vbo);
		glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);
		glDrawArrays(GL_LINES, 0, lines.size() * 2);
		glBindVertexArray(0);
	}

	@Override
	public void destroy () {
		if (_vbo != 0)
			glDeleteBuffers(_vbo);
		if (_vao != 0)
			glDeleteVertexArrays(_vao);
		if (_shader != null)
			_shader.destroy();

		_vbo = 0;
		_vao = 0;
		_shader = null;
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

	private void putVertex (FloatBuffer buffer, float x, float y, float z, float r, float g, float b, float a) {
		buffer.put(x).put(y).put(z).put(r).put(g).put(b).put(a);
	}

	private String debugVertexShader () {
		return """
				#version 330 core
				layout (location = 0) in vec3 aPosition;
				layout (location = 1) in vec4 aColor;
				uniform mat4 u_view;
				uniform mat4 u_projection;
				out vec4 v_color;
				void main () {
					gl_Position = u_projection * u_view * vec4(aPosition, 1.0);
					v_color = aColor;
				}
				""";
	}

	private String debugFragmentShader () {
		return """
				#version 330 core
				in vec4 v_color;
				void main () {
					gl_FragColor = v_color;
				}
				""";
	}
}
