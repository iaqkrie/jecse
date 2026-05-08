package qchromatic.jecse.engine;

import qchromatic.jecse.common.Vec2;

public final class Input {
	private Input () { }

	private static InputState _current = new InputState();

	public static InputState current () { return _current; }

	public static void use (InputState input) {
		if (input == null)
			throw new IllegalArgumentException("Input state cannot be null");

		_current = input;
	}

	public static void update() {
		_current.update();
	}

	public static void reset () {
		_current.reset();
	}

	public static void setKeyState(int key, boolean pressed) {
		_current.setKeyState(key, pressed);
	}

	public static void setMouseButtonState(int button, boolean pressed) {
		_current.setMouseButtonState(button, pressed);
	}

	public static void setMousePosition(float x, float y) {
		_current.setMousePosition(x, y);
	}

	public static void setScrollOffset(float xoffset, float yoffset) {
		_current.setScrollOffset(xoffset, yoffset);
	}

	public static boolean getKey (int key) {
		return _current.getKey(key);
	}

	public static boolean getKeyDown (int key) {
		return _current.getKeyDown(key);
	}

	public static boolean getKeyUp (int key) {
		return _current.getKeyUp(key);
	}

	public static boolean getMouseButton (int button) {
		return _current.getMouseButton(button);
	}

	public static boolean getMouseButtonDown (int button) {
		return _current.getMouseButtonDown(button);
	}

	public static boolean getMouseButtonUp (int button) {
		return _current.getMouseButtonUp(button);
	}

	public static Vec2 getMousePosition () {
		return _current.getMousePosition();
	}

	public static Vec2 getMouseDelta () {
		return _current.getMouseDelta();
	}

	public static Vec2 getMouseScroll () {
		return _current.getMouseScroll();
	}
}
