package qchromatic.jecse.engine;

import qchromatic.jecse.common.Vec2;

public final class Input {
	private static final int MAX_KEYS = 512;
	private static final int MAX_MOUSE_BUTTONS = 16;

	private static boolean[] _keys = new boolean[MAX_KEYS];
	private static boolean[] _keysPrev = new boolean[MAX_KEYS];
	private static boolean[] _mouseButtons = new boolean[MAX_MOUSE_BUTTONS];
	private static boolean[] _mouseButtonsPrev = new boolean[MAX_MOUSE_BUTTONS];
	private static float _mouseX;
	private static float _mouseY;
	private static float _mouseDX;
	private static float _mouseDY;
	private static float _scrollX;
	private static float _scrollY;

	public static void update() {
		System.arraycopy(_keys, 0, _keysPrev, 0, MAX_KEYS);
		System.arraycopy(_mouseButtons, 0, _mouseButtonsPrev, 0, MAX_MOUSE_BUTTONS);
		_mouseDX = 0f;
		_mouseDY = 0f;
		_scrollX = 0f;
		_scrollY = 0f;
	}

	public static void setKeyState(int key, boolean pressed) {
		if (key >= 0 && key < MAX_KEYS) _keys[key] = pressed;
	}

	public static void setMouseButtonState(int button, boolean pressed) {
		if (button >= 0 && button < MAX_MOUSE_BUTTONS) _mouseButtons[button] = pressed;
	}

	public static void setMousePosition(float x, float y) {
		_mouseDX = x - _mouseX;
		_mouseDY = y - _mouseY;
		_mouseX = x;
		_mouseY = y;
	}

	public static void setScrollOffset(float xoffset, float yoffset) {
		_scrollX = xoffset;
		_scrollY = yoffset;
	}

	public static boolean getKey (int key) {
		return key >= 0 && key < MAX_KEYS && _keys[key];
	}
	public static boolean getKeyDown (int key) {
		return key >= 0 && key < MAX_KEYS && _keys[key] && !_keysPrev[key];
	}
	public static boolean getKeyUp (int key) {
		return key >= 0 && key < MAX_KEYS && !_keys[key] && _keysPrev[key];
	}

	public static boolean getMouseButton (int button) {
		return button >= 0 && button < MAX_MOUSE_BUTTONS && _mouseButtons[button];
	}
	public static boolean getMouseButtonDown (int button) {
		return button >= 0 && button < MAX_MOUSE_BUTTONS && _mouseButtons[button] && !_mouseButtonsPrev[button];
	}
	public static boolean getMouseButtonUp (int button) {
		return button >= 0 && button < MAX_MOUSE_BUTTONS && !_mouseButtons[button] && _mouseButtonsPrev[button];
	}

	public static Vec2 getMousePosition () {
		return new Vec2(_mouseX, _mouseY);
	}

	public static Vec2 getMouseDelta () {
		return new Vec2(_mouseDX, _mouseDY);
	}

	public static Vec2 getMouseScroll () {
		return new Vec2(_scrollX, _scrollY);
	}
}
