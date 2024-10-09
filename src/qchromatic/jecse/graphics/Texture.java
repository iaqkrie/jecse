package qchromatic.jecse.graphics;

import org.lwjgl.system.MemoryStack;
import qchromatic.jecse.math.Vec2;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {
	private final Vec2 _size;

	int texture;

	public Texture (String path) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer width = stack.mallocInt(1);
			IntBuffer height = stack.mallocInt(1);
			IntBuffer channels = stack.mallocInt(1);

			ByteBuffer image = stbi_load(path, width, height, channels, 4);
			if (image == null) {
				throw new RuntimeException("Failed to load a texture file");
			}

			_size = new Vec2(width.get(), height.get());

			texture = glGenTextures();
			glBindTexture(GL_TEXTURE_2D, texture);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

			glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, _size.x, _size.y, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);

			glBindTexture(GL_TEXTURE_2D, 0);
			stbi_image_free(image);
		}
	}

	public Vec2 getSize () {
		return new Vec2(_size);
	}
}
