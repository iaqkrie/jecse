package qchromatic.jecse.graphics;

import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL33;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import qchromatic.jecse.core.InputInfo;
import qchromatic.jecse.math.Vec2;

import org.lwjgl.glfw.GLFWErrorCallback;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;

public final class Window {
	private static final Vec2 DEFAULT_SIZE = new Vec2(800, 450);
	private static final String DEFAULT_TITLE = "jecse app";

	private final long _hwnd;

	public Window () { this(DEFAULT_SIZE, DEFAULT_TITLE); }
	public Window (String title) { this(DEFAULT_SIZE, title); }
	public Window (Vec2 size, String title) {
		GLFWErrorCallback.createPrint(System.err).set();

		if (!glfwInit())
			throw new IllegalStateException("GLFW init error!");

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_VISIBLE, 0);
		glfwWindowHint(GLFW_RESIZABLE, 1);

		_hwnd = glfwCreateWindow(size.x, size.y, title, 0, 0);
		if (_hwnd == 0)
			throw new RuntimeException("Window creation error!");

		glfwSetFramebufferSizeCallback(_hwnd, (_, width, height) -> {
			GL33.glViewport(0, 0, width, height);
		});

		glfwSetKeyCallback(_hwnd, (_, key, _, action, _) -> {
			if (action == GLFW_PRESS)
				InputInfo.pressedKeys.add(key);
			else if (action == GLFW_RELEASE)
				InputInfo.pressedKeys.remove((Integer) key);
		});

		glfwSetMouseButtonCallback(_hwnd, (_, button, action, _) -> {
			if (action == GLFW_PRESS)
				InputInfo.pressedMouseButtons.add(button);
			else if (action == GLFW_RELEASE)
				InputInfo.pressedMouseButtons.remove((Integer) button);
		});

		glfwSetCursorPosCallback(_hwnd, (_, xpos, ypos) -> {
			InputInfo.mousePosition.x = (float) xpos;
			InputInfo.mousePosition.y = (float) ypos;
		});

		glfwMakeContextCurrent(_hwnd);
		GL.createCapabilities();
	}

	public long getHandler () { return _hwnd; }

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

	public void setIcon (String iconPath) {
		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer width = stack.mallocInt(1);
			IntBuffer height = stack.mallocInt(1);
			IntBuffer channels = stack.mallocInt(1);

			ByteBuffer icon = STBImage.stbi_load(iconPath, width, height, channels, 4);
			if (icon == null) {
				throw new RuntimeException("Failed to load icon image: " + STBImage.stbi_failure_reason());
			}

			GLFWImage.Buffer iconBuffer = GLFWImage.malloc(1);
			iconBuffer.position(0).width(width.get(0)).height(height.get(0)).pixels(icon);

			glfwSetWindowIcon(_hwnd, iconBuffer);

			STBImage.stbi_image_free(icon);
		}
	}

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
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		glfwShowWindow(_hwnd);
	}

	public void close () {
		glfwSetWindowShouldClose(_hwnd, true);
	}
}
