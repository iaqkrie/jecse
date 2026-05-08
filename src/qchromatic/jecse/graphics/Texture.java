package qchromatic.jecse.graphics;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import qchromatic.jecse.core.Disposable;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL13.*;

public class Texture implements Disposable {
	private final int _id;

	private final int _width;
	private final int _height;
	private byte[] _data;

	public Texture (int width, int height) {
		if (width <= 0 || height <= 0)
			throw new IllegalArgumentException("Texture dimensions must be positive");

		_id = glGenTextures();

		_width = width;
		_height = height;
		_data = new byte[_width * _height * 4];
	}

	private Texture (int width, int height, ByteBuffer pixels) {
		if (width <= 0 || height <= 0)
			throw new IllegalArgumentException("Texture dimensions must be positive");
		if (pixels == null)
			throw new IllegalArgumentException("Pixels cannot be null");

		_id = glGenTextures();
		_width = width;
		_height = height;
		_data = null;

		uploadBufferToGPU(pixels);
	}

	public static Texture load (Path path) {
		if (path == null)
			throw new IllegalArgumentException("Texture path cannot be null");

		IntBuffer width = BufferUtils.createIntBuffer(1);
		IntBuffer height = BufferUtils.createIntBuffer(1);
		IntBuffer channels = BufferUtils.createIntBuffer(1);

		STBImage.stbi_set_flip_vertically_on_load(true);
		ByteBuffer pixels = STBImage.stbi_load(path.toAbsolutePath().toString(), width, height, channels, 4);
		if (pixels == null)
			throw new RuntimeException("Failed to load texture " + path + ": " + STBImage.stbi_failure_reason());

		try {
			return new Texture(width.get(0), height.get(0), pixels);
		} finally {
			STBImage.stbi_image_free(pixels);
		}
	}

	public static Texture debugTexture () {
		Texture result = new Texture(2, 2);
		result.setPixel(0, 0, 0f, 0f, 0f, 1f);
		result.setPixel(0, 1, 1f, 0f, 1f, 1f);
		result.setPixel(1, 0, 1f, 0f, 1f, 1f);
		result.setPixel(1, 1, 0f, 0f, 0f, 1f);
		result.uploadToGPU();
		return result;
	}

	public void uploadToGPU () {
		if (_data == null)
			throw new IllegalStateException("Texture was created from GPU-only image data");

		ByteBuffer data = BufferUtils.createByteBuffer(_data.length);
		data.put(_data);
		data.flip();

		uploadBufferToGPU(data);
	}

	public void setPixel (int x, int y, float r, float g, float b, float a) {
		if (_data == null)
			throw new IllegalStateException("Texture pixel data is not CPU-readable");
		if (x < 0 || x >= _width || y < 0 || y >= _height)
			throw new IndexOutOfBoundsException("Texture pixel is out of bounds");

		int index = (y * _width + x) * 4;
		_data[index] = (byte) (r * 255);
		_data[index + 1] = (byte) (g * 255);
		_data[index + 2] = (byte) (b * 255);
		_data[index + 3] = (byte) (a * 255);
	}

	public void bind (int textureUnit) {
		glActiveTexture(GL_TEXTURE0 + textureUnit);
		glBindTexture(GL_TEXTURE_2D, _id);
	}

	public void unbind () {
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public int id () { return _id; }

	public int width () { return _width; }

	public int height () { return _height; }

	@Override
    public void destroy () {
		glDeleteTextures(_id);
	}

	private void uploadBufferToGPU (ByteBuffer data) {
		glBindTexture(GL_TEXTURE_2D, _id);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, _width, _height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);

		glBindTexture(GL_TEXTURE_2D, 0);
	}
}
