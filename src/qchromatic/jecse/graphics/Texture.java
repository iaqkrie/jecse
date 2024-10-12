package qchromatic.jecse.graphics;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;
import qchromatic.jecse.math.Vec2;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture {
	private final Vec2 _size;
	private final byte[] _data;

	int texture;

	public Texture (Vec2 size) {
		_size = new Vec2(size);
		_data = new byte[size.x * size.y * 4];
	}
	public Texture (String path) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer width = stack.mallocInt(1);
			IntBuffer height = stack.mallocInt(1);
			IntBuffer channels = stack.mallocInt(1);

			ByteBuffer image = stbi_load(path, width, height, channels, 4);
			if (image == null) {
				throw new RuntimeException("Failed to load a texture file: " + path);
			}

			_size = new Vec2(width.get(), height.get());
			_data = new byte[image.remaining()];
			image.get(_data);

			stbi_image_free(image);
		}
	}

	public void createOnGPU () {
		texture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, texture);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		ByteBuffer data = BufferUtils.createByteBuffer(_data.length);
		data.put(_data);
		data.flip();

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, _size.x, _size.y, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);

		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public void setPixel (int x, int y, byte r, byte g, byte b, byte a) {
		int index = (y * _size.x + x) * 4;
		_data[index] = r;
		_data[index + 1] = g;
		_data[index + 2] = b;
		_data[index + 3] = a;
	}

	public Vec2 getSize () { return new Vec2(_size); }

	public void delete () {
		glDeleteTextures(texture);
	}
}
