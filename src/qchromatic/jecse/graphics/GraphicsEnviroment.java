package qchromatic.jecse.graphics;

import qchromatic.jecse.core.Color;
import qchromatic.jecse.core.Game;
import qchromatic.jecse.math.Mat3f;
import qchromatic.jecse.core.TextureManager;

import java.nio.file.Paths;

import static org.lwjgl.opengl.GL33.*;

public final class GraphicsEnviroment {
	public static final String DEFAULT_VERTEX_SHADER_SOURCE =
			"""
			#version 330
			
			layout(location = 0) in vec2 aPos;
			layout(location = 1) in vec2 aTexCoord;
			
			out vec2 texCoord;
			
			uniform mat3 model;
			uniform mat3 view;
			uniform mat3 projection;
			
			void main() {
			    vec3 tPos = projection * view * model * vec3(aPos, 1);
			    gl_Position = vec4(tPos.xy, 0, 1);
			
			    texCoord = aTexCoord;
			}
			""";
	public static final String DEFAULT_FRAGMENT_SHADER_SOURCE =
			"""
			#version 330
			
			in vec2 texCoord;
			
			uniform vec4 color;
			uniform sampler2D tex;
			
			void main() {
			    gl_FragColor = color * texture(tex, texCoord);
			}
			""";

	public static final float DEFAULT_UNIT_SIZE = 100f;

	private static final float[] _GRAPHICS_DATA = {
			// pos     // tex
			-0.5f,  0.5f,  0f, 0f, // tl
			 0.5f,  0.5f,  1f, 0f, // tr
			-0.5f, -0.5f,  0f, 1f, // bl
			 0.5f, -0.5f,  1f, 1f  // br
	};

	private static final int[] _INDICES = {
			0, 1, 2,
			1, 3, 2
	};

	private static int _mainVao;

	private static int _mainVbo;
	private static int _mainEbo;

	private static ShaderProgram _shaders;

	public static void init () {
		glClearColor(0f, 0f, 0f, 1f);

		_mainVao = glGenVertexArrays();
		glBindVertexArray(_mainVao);

		_mainVbo = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, _mainVbo);
		glBufferData(GL_ARRAY_BUFFER, _GRAPHICS_DATA, GL_STATIC_DRAW);

		glVertexAttribPointer(0, 2, GL_FLOAT, false, 4 * Float.BYTES, 0);
		glEnableVertexAttribArray(0);

		glVertexAttribPointer(1, 2, GL_FLOAT, false, 4 * Float.BYTES, 2 * Float.BYTES);
		glEnableVertexAttribArray(1);

		_mainEbo = glGenBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, _mainEbo);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, _INDICES, GL_STATIC_DRAW);

		_shaders = new ShaderProgram(
				DEFAULT_VERTEX_SHADER_SOURCE,
				DEFAULT_FRAGMENT_SHADER_SOURCE
		);

		_shaders.use();
	}

	public static void clear () { glClear(GL_COLOR_BUFFER_BIT); }

	public static void render (Mat3f model, Mat3f view, Mat3f projection, Color color, Texture texture) {
		model = model == null ? new Mat3f() : model;
		view = view == null ? new Mat3f() : view;
		projection = projection == null ? new Mat3f() : projection;
		color = color == null ? Color.WHITE : color;
		texture = texture == null ? TextureManager.getTexture(0) : texture;

		int modelUf = glGetUniformLocation(_shaders.getHandler(), "model");
		int viewUf = glGetUniformLocation(_shaders.getHandler(), "view");
		int projectionUf = glGetUniformLocation(_shaders.getHandler(), "projection");

		int colorUf = glGetUniformLocation(_shaders.getHandler(), "color");
		int textureUf = glGetUniformLocation(_shaders.getHandler(), "tex");

		glUniformMatrix3fv(modelUf, false, model.getMatrix());
		glUniformMatrix3fv(viewUf, false, view.getMatrix());
		glUniformMatrix3fv(projectionUf, false, projection.getMatrix());
		glUniform4f(colorUf, color.r, color.g, color.b, color.a);

		glActiveTexture(GL_TEXTURE0);
		glBindTexture(GL_TEXTURE_2D, texture.texture);
		glUniform1i(textureUf, 0);

		glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);

		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public static void finalise () {
		ShaderProgram.clearActiveProgram();
		_shaders.delete();

		glBindVertexArray(0);
		glDeleteVertexArrays(_mainVao);

		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glDeleteBuffers(_mainVbo);

		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		glDeleteBuffers(_mainEbo);
	}

	public static Mat3f getProjectionMatrix () {
		float horizontal = Game.window.getSize().x / DEFAULT_UNIT_SIZE;
		float vertical = Game.window.getSize().y / DEFAULT_UNIT_SIZE;

		return Mat3f.ortho(-horizontal / 2, horizontal / 2, -vertical / 2, vertical / 2);
	}

	public static Mat3f getUIProjectionMatrix () {
		float width = Game.window.getSize().x;
		float height = Game.window.getSize().y;

		return Mat3f.ortho(0, width, height, 0);
	}
}
