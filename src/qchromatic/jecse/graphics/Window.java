package qchromatic.jecse.graphics;

import org.lwjgl.opengl.GL;
import qchromatic.jecse.math.Vec2;

import org.lwjgl.glfw.GLFWErrorCallback;

import static org.lwjgl.glfw.GLFW.*;

public final class Window {
	private static final Vec2 DEFAULT_SIZE = new Vec2(800, 450);
	private static final String DEFAULT_TITLE = "jecse app";

	private long _hwnd;

	public Window () { this(DEFAULT_SIZE, DEFAULT_TITLE); }
	public Window (String title) { this(DEFAULT_SIZE, title); }
	public Window (Vec2 size, String title) {
		GLFWErrorCallback.createPrint(System.err).set();

		if (!glfwInit()) {
			System.err.println("GLFW init error!");
			return;
		}

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, 0);
		glfwWindowHint(GLFW_RESIZABLE, 0);

		_hwnd = glfwCreateWindow(size.x, size.y, title, 0, 0);
		if (_hwnd == 0) {
			System.err.println("Window init error!");
			return;
		}
	}

	public Vec2 getSize () {
		int[] width = new int[1];
		int[] height = new int[1];
		glfwGetWindowSize(_hwnd, width, height);
		return new Vec2(width[0], height[0]);
	}
	public void setSize (Vec2 size) { setSize(size.x, size.y); }
	public void setSize (int width, int height) { glfwSetWindowSize(_hwnd, width, height); }

	public Vec2 getPosition () {
		int[] x = new int[1];
		int[] y = new int[1];
		glfwGetWindowPos(_hwnd, x, y);
		return new Vec2(x[0], y[0]);
	}
	public void setPosition (Vec2 position) { setPosition(position.x, position.y); }
	public void setPosition (int x, int y) { glfwSetWindowPos(_hwnd, x, y); }

	public boolean shouldClose () {
		return glfwWindowShouldClose(_hwnd);
	}

	public void pollEvents () {
		glfwPollEvents();
	}

	public void swapBuffers () {
		glfwSwapBuffers(_hwnd);
	}

	public void show () {
		glfwMakeContextCurrent(_hwnd);
		GL.createCapabilities();

		glfwShowWindow(_hwnd);
	}

	public void close () {
		glfwSetWindowShouldClose(_hwnd, true);
	}
}
