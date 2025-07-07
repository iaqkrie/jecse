package qchromatic.jecse.graphics;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.glfw.GLFW.*;

public final class Window {
	private static final int DEAFULT_WIDTH = 800;
	private static final int DEAFULT_HEIGHT = 450;
	private static final String DEFAULT_TITLE = "jecse app";
	private static final boolean DEFAULT_RESIZABLE = true;

	private long _hwnd;

	private int _width;
	private int _height;
	private String _title;
	private boolean _resizable;

	public Window (int width, int height, String title) {
		_width = width;
		_height = height;
		_title = title;
		_resizable = DEFAULT_RESIZABLE;

		initGLFW();
		createWindow();
		initOpenGL();
	}
	public Window () { this(DEAFULT_WIDTH, DEAFULT_HEIGHT, DEFAULT_TITLE); }

	private void initGLFW () {
		if (!glfwInit())
			throw new RuntimeException("Failed to init GLFW"); // TODO

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_RESIZABLE, _resizable ? 1 : 0);
		glfwWindowHint(GLFW_VISIBLE, 0);
	}

	private void createWindow () {
		_hwnd = glfwCreateWindow(_width, _height, _title, 0, 0);
		if (_hwnd == 0)
			throw new RuntimeException("Failed to create window"); // TODO

		glfwMakeContextCurrent(_hwnd);
	}

	private void initOpenGL () {
		GL.createCapabilities();
	}

	private void initCallbacks () {
		// TODO
	}

	public void show () {
		glfwShowWindow(_hwnd);
	}

	public void hide () {
		glfwHideWindow(_hwnd);
	}

	public void close () {
		glfwSetWindowShouldClose(_hwnd, true);
	}

	public void update () {
		glfwSwapBuffers(_hwnd);
		glfwPollEvents();
	}

	public boolean shouldClose () {
		return glfwWindowShouldClose(_hwnd);
	}

	public void destroy () {
		glfwDestroyWindow(_hwnd);
		glfwTerminate();
	}
}
