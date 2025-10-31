package qchromatic.jecse.graphics;

import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.opengl.GL;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public final class Window {
	private static final int DEAFULT_WIDTH = 800;
	private static final int DEAFULT_HEIGHT = 450;
	private static final String DEFAULT_TITLE = "jecse app";
	private static final boolean DEFAULT_RESIZABLE = true;
	private static final boolean DEFAULT_VSYNC = false;

	private long _hwnd;

	private int _width;
	private int _height;
	private String _title;
	private boolean _resizable;
	private boolean _vsync;

	public Window (int width, int height, String title) {
		_width = width;
		_height = height;
		_title = title;
		_resizable = DEFAULT_RESIZABLE;
		_vsync = DEFAULT_VSYNC;

		initGLFW();
		createWindow();
		initOpenGL();
	}
	public Window () { this(DEAFULT_WIDTH, DEAFULT_HEIGHT, DEFAULT_TITLE); }
	public Window (int width, int height) { this(width, height, DEFAULT_TITLE); }

	private void initGLFW () {
		if (!glfwInit())
			throw new RuntimeException("Failed to init GLFW");

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, 0);
		glfwWindowHint(GLFW_RESIZABLE, _resizable ? 1 : 0);
	}

	private void createWindow () {
		_hwnd = glfwCreateWindow(_width, _height, _title, 0, 0);
		if (_hwnd == 0)
			throw new RuntimeException("Failed to create window");

		glfwMakeContextCurrent(_hwnd);
	}

	private void initOpenGL () {
		GL.createCapabilities();

		if (_vsync)
			glfwSwapInterval(1);

		glClearColor(0f, 0f, 0f, 1f);
		glEnable(GL_DEPTH_TEST);
		glEnable(GL_CULL_FACE);
		glCullFace(GL_BACK);
	}

	private void setupCallbacks () {
		glfwSetFramebufferSizeCallback(_hwnd, (window, newW, newH) -> {
			_width = newW;
			_height = newH;
			glViewport(0, 0, _width, _height);
		});
	}

	public void show () { glfwShowWindow(_hwnd); }
	public void hide () { glfwHideWindow(_hwnd); }

	public void clear () { glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); }

	public void update () {
		glfwSwapBuffers(_hwnd);
		glfwPollEvents();
	}

	public void destroy () {
		glfwDestroyWindow(_hwnd);
		glfwTerminate();
	}

	public void close () { glfwSetWindowShouldClose(_hwnd, true); }
	public boolean shouldClose () { return glfwWindowShouldClose(_hwnd); }
}
