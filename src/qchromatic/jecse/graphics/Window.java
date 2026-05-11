package qchromatic.jecse.graphics;

import org.lwjgl.opengl.GL;
import qchromatic.jecse.common.Vec4;
import qchromatic.jecse.engine.Input;
import qchromatic.jecse.engine.InputState;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public final class Window {
	private static final int DEFAULT_WIDTH = 800;
	private static final int DEFAULT_HEIGHT = 450;
	private static final String DEFAULT_TITLE = "jecse app";
	private static final boolean DEFAULT_RESIZABLE = true;
	private static final boolean DEFAULT_VSYNC = false;

	private static boolean _glfwInitialized;
	private static int _windowCount;

	private final InputState _input;
	private final List<WindowResizeListener> _resizeListeners;
	private long _hwnd;

	private int _width;
	private int _height;
	private String _title;
	private boolean _resizable;
	private boolean _vsync;

	public Window () { this(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_TITLE, Input.current()); }
	public Window (InputState input) { this(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_TITLE, input); }
	public Window (String title) { this(DEFAULT_WIDTH, DEFAULT_HEIGHT, title, Input.current()); }
	public Window (String title, InputState input) { this(DEFAULT_WIDTH, DEFAULT_HEIGHT, title, input); }
	public Window (int width, int height) { this(width, height, DEFAULT_TITLE, Input.current()); }
	public Window (int width, int height, InputState input) { this(width, height, DEFAULT_TITLE, input); }
	public Window (int width, int height, String title) { this(width, height, title, Input.current()); }
	public Window (int width, int height, String title, InputState input) {
		if (input == null)
			throw new IllegalArgumentException("Input state cannot be null");

		_input = input;
		_resizeListeners = new ArrayList<>();
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
		if (!_glfwInitialized) {
			if (!glfwInit())
				throw new RuntimeException("Failed to init GLFW");

			_glfwInitialized = true;
		}

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, 0);
		glfwWindowHint(GLFW_RESIZABLE, _resizable ? 1 : 0);
	}

	private void createWindow () {
		_hwnd = glfwCreateWindow(_width, _height, _title, 0, 0);
		if (_hwnd == 0)
			throw new RuntimeException("Failed to create window");

		_windowCount++;
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
			notifyResizeListeners();
		});

		glfwSetKeyCallback(_hwnd, (window, key, scancode, action, mods) -> {
			_input.setKeyState(key, action != GLFW_RELEASE);
		});

		glfwSetMouseButtonCallback(_hwnd, (window, button, action, mods) -> {
			_input.setMouseButtonState(button, action != GLFW_RELEASE);
		});

		glfwSetCursorPosCallback(_hwnd, (window, xpos, ypos) -> {
			_input.setMousePosition((float) xpos, (float) ypos);
		});

		glfwSetCursorEnterCallback(_hwnd, (window, entered) -> {
			if (!entered)
				_input.resetMousePositionTracking();
		});

		glfwSetScrollCallback(_hwnd, (window, xoffset, yoffset) -> {
			_input.setScrollOffset((float) xoffset, (float) yoffset);
		});
	}

	public InputState input () { return _input; }

	public int width () { return _width; }

	public int height () { return _height; }

	public float aspectRatio () {
		return _height == 0 ? 1f : (float) _width / (float) _height;
	}

	public void addResizeListener (WindowResizeListener listener) {
		if (listener != null && !_resizeListeners.contains(listener))
			_resizeListeners.add(listener);
	}

	public void removeResizeListener (WindowResizeListener listener) {
		_resizeListeners.remove(listener);
	}

	public void setCursorNormal () {
		setCursorMode(GLFW_CURSOR_NORMAL);
	}
	public void setCursorDisabled () {
		setCursorMode(GLFW_CURSOR_DISABLED);
	}
	public void setCursorHidden () {
		setCursorMode(GLFW_CURSOR_HIDDEN);
	}
	public void setCursorCaptured () {
		setCursorMode(GLFW_CURSOR_CAPTURED);
	}

	private void setCursorMode (int mode) {
		glfwSetInputMode(_hwnd, GLFW_CURSOR, mode);
		_input.resetMousePositionTracking();
	}

	public void show () { glfwShowWindow(_hwnd); }
	public void hide () { glfwHideWindow(_hwnd); }

	public void clear () { glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); }

	public void clear (Vec4 color) {
		if (color != null)
			glClearColor(color.x, color.y, color.z, color.w);

		clear();
	}

	public void update () {
		glfwSwapBuffers(_hwnd);
		glfwPollEvents();
	}

	public void destroy () {
		if (_hwnd == 0) return;

		glfwDestroyWindow(_hwnd);
		_hwnd = 0;
		_resizeListeners.clear();

		_windowCount = Math.max(0, _windowCount - 1);
		if (_windowCount == 0 && _glfwInitialized) {
			glfwTerminate();
			_glfwInitialized = false;
		}
	}

	public void close () { glfwSetWindowShouldClose(_hwnd, true); }
	public boolean shouldClose () { return glfwWindowShouldClose(_hwnd); }

	private void notifyResizeListeners () {
		for (WindowResizeListener listener : List.copyOf(_resizeListeners))
			listener.onResize(this, _width, _height);
	}
}
