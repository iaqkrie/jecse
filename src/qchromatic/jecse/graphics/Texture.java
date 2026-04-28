package qchromatic.jecse.graphics;

import org.lwjgl.BufferUtils;
import qchromatic.jecse.core.Disposable;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL13.*;

public class Texture implements Disposable {
	private final int _id;

	private final int _width;
	private final int _height;
	private final byte[] _data;

	public Texture (int width, int height) {
		_id = glGenTextures();

		_width = width;
		_height = height;
		_data = new byte[_width * _height * 4];
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
		glBindTexture(GL_TEXTURE_2D, _id);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

		ByteBuffer data = BufferUtils.createByteBuffer(_data.length);
		data.put(_data);
		data.flip();

		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, _width, _height, 0, GL_RGBA, GL_UNSIGNED_BYTE, data);

		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public void setPixel (int x, int y, float r, float g, float b, float a) {
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

	public void destroy () {
		glDeleteTextures(_id);
	}
}
