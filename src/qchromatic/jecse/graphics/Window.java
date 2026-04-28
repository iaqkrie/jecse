package qchromatic.jecse.graphics;

import org.lwjgl.opengl.GL;
import qchromatic.jecse.engine.Input;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public final class Window {
	private static final int DEFAULT_WIDTH = 800;
	private static final int DEFAULT_HEIGHT = 450;
	private static final String DEFAULT_TITLE = "jecse app";
	private static final boolean DEFAULT_RESIZABLE = true;
	private static final boolean DEFAULT_VSYNC = false;

	private long _hwnd;

	private int _width;
	private int _height;
	private String _title;
	private boolean _resizable;
	private boolean _vsync;

	public Window () { this(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_TITLE); }
	public Window (String title) { this(DEFAULT_WIDTH, DEFAULT_HEIGHT, title); }
	public Window (int width, int height) { this(width, height, DEFAULT_TITLE); }
	public Window (int width, int height, String title) {
		_width = width;
		_height = height;
		_title = title;
		_resizable = DEFAULT_RESIZABLE;
		_vsync = DEFAULT_VSYNC;

		initGLFW();
		createWindow();
		initOpenGL();
		setupCallbacks();
	}

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

		glClearColor(0.2f, 0.1f, 0.2f, 1f);
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

		glfwSetKeyCallback(_hwnd, (window, key, scancode, action, mods) -> {
			Input.setKeyState(key, action != GLFW_RELEASE);
		});

		glfwSetMouseButtonCallback(_hwnd, (window, button, action, mods) -> {
			Input.setMouseButtonState(button, action != GLFW_RELEASE);
		});

		glfwSetCursorPosCallback(_hwnd, (window, xpos, ypos) -> {
			Input.setMousePosition((float) xpos, (float) ypos);
		});

		glfwSetScrollCallback(_hwnd, (window, xoffset, yoffset) -> {
			Input.setScrollOffset((float) xoffset, (float) yoffset);
		});
	}

	public void setCursorNormal () {
		glfwSetInputMode(_hwnd, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
	}
	public void setCursorDisabled () {
		glfwSetInputMode(_hwnd, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
	}
	public void setCursorHidden () {
		glfwSetInputMode(_hwnd, GLFW_CURSOR, GLFW_CURSOR_HIDDEN);
	}
	public void setCursorCaptured () {
		glfwSetInputMode(_hwnd, GLFW_CURSOR, GLFW_CURSOR_CAPTURED);
	}

	public void show () { glfwShowWindow(_hwnd); }
	public void hide () { glfwHideWindow(_hwnd); }

	public void clear () { glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); }

	public void update () {
		glfwSwapBuffers(_hwnd);
		glfwPollEvents();
	}

	public void destroy () {
		if (_hwnd == 0) return;

		glfwDestroyWindow(_hwnd);
		_hwnd = 0;

		glfwTerminate();
	}

	public void close () { glfwSetWindowShouldClose(_hwnd, true); }
	public boolean shouldClose () { return glfwWindowShouldClose(_hwnd); }
}
