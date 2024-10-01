package iaqkrie.jecse.graphics;

import iaqkrie.jecse.Vec2;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.glfw.GLFW.*;

public class Window {
	public static final Vec2 DEFAULT_SIZE = new Vec2(800, 450);
	public static final String DEFAULT_TITLE = "jecse app";

	private Vec2 _size;
	private Vec2 _position;
	private String _title;
	private boolean _resizable;

	private long _hwnd;

	public Window () { this(DEFAULT_SIZE, DEFAULT_TITLE); }
	public Window (Vec2 size, String title) {
		_size = new Vec2(size);
		_position = new Vec2();
		_title = title;
		_resizable = false;

		GLFWErrorCallback.createPrint(System.err).set();

		if (!glfwInit()) {
			System.err.println("GLFW init error!");
			return;
		}

		_hwnd = glfwCreateWindow(_size.x, _size.y, _title, 0, 0);
		if (_hwnd == 0) {
			System.err.println("Window init error!");
			return;
		}

		glfwMakeContextCurrent(_hwnd);
		GL.createCapabilities();

		GL11.glClearColor(0f, 0f, 0f, 0f);

		while (!glfwWindowShouldClose(_hwnd)) {
			GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

			glfwSwapBuffers(_hwnd);
			glfwPollEvents();
		}
	}

	public void test () {
		int[] width = new int[1];
		int[] height = new int[1];
		glfwGetWindowSize(_hwnd, width, height);
		System.out.println("w: " + width[0] + "\nh: " + height[0]);
	}
}
