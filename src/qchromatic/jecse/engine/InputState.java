package qchromatic.jecse.engine;

import qchromatic.jecse.common.Vec2;

public final class InputState {
	private static final int MAX_KEYS = 512;
	private static final int MAX_MOUSE_BUTTONS = 16;

	private final boolean[] _keys;
	private final boolean[] _keysPrev;
	private final boolean[] _mouseButtons;
	private final boolean[] _mouseButtonsPrev;

	private float _mouseX;
	private float _mouseY;
	private float _mouseDX;
	private float _mouseDY;
	private boolean _hasMousePosition;
	private float _scrollX;
	private float _scrollY;

	public InputState () {
		_keys = new boolean[MAX_KEYS];
		_keysPrev = new boolean[MAX_KEYS];
		_mouseButtons = new boolean[MAX_MOUSE_BUTTONS];
		_mouseButtonsPrev = new boolean[MAX_MOUSE_BUTTONS];
	}

	public void update() {
		java.lang.System.arraycopy(_keys, 0, _keysPrev, 0, MAX_KEYS);
		java.lang.System.arraycopy(_mouseButtons, 0, _mouseButtonsPrev, 0, MAX_MOUSE_BUTTONS);
		_mouseDX = 0f;
		_mouseDY = 0f;
		_scrollX = 0f;
		_scrollY = 0f;
	}

	public void reset () {
		for (int i = 0; i < MAX_KEYS; i++) {
			_keys[i] = false;
			_keysPrev[i] = false;
		}

		for (int i = 0; i < MAX_MOUSE_BUTTONS; i++) {
			_mouseButtons[i] = false;
			_mouseButtonsPrev[i] = false;
		}

		_mouseDX = 0f;
		_mouseDY = 0f;
		_mouseX = 0f;
		_mouseY = 0f;
		_hasMousePosition = false;
		_scrollX = 0f;
		_scrollY = 0f;
	}

	public void setKeyState(int key, boolean pressed) {
		if (key >= 0 && key < MAX_KEYS) _keys[key] = pressed;
	}

	public void setMouseButtonState(int button, boolean pressed) {
		if (button >= 0 && button < MAX_MOUSE_BUTTONS) _mouseButtons[button] = pressed;
	}

	public void setMousePosition(float x, float y) {
		if (!_hasMousePosition) {
			_mouseX = x;
			_mouseY = y;
			_mouseDX = 0f;
			_mouseDY = 0f;
			_hasMousePosition = true;
			return;
		}

		_mouseDX += x - _mouseX;
		_mouseDY += y - _mouseY;
		_mouseX = x;
		_mouseY = y;
	}

	public void setScrollOffset(float xoffset, float yoffset) {
		_scrollX += xoffset;
		_scrollY += yoffset;
	}

	public void resetMousePositionTracking () {
		_hasMousePosition = false;
		_mouseDX = 0f;
		_mouseDY = 0f;
	}

	public boolean getKey (int key) {
		return key >= 0 && key < MAX_KEYS && _keys[key];
	}

	public boolean getKeyDown (int key) {
		return key >= 0 && key < MAX_KEYS && _keys[key] && !_keysPrev[key];
	}

	public boolean getKeyUp (int key) {
		return key >= 0 && key < MAX_KEYS && !_keys[key] && _keysPrev[key];
	}

	public boolean getMouseButton (int button) {
		return button >= 0 && button < MAX_MOUSE_BUTTONS && _mouseButtons[button];
	}

	public boolean getMouseButtonDown (int button) {
		return button >= 0 && button < MAX_MOUSE_BUTTONS && _mouseButtons[button] && !_mouseButtonsPrev[button];
	}

	public boolean getMouseButtonUp (int button) {
		return button >= 0 && button < MAX_MOUSE_BUTTONS && !_mouseButtons[button] && _mouseButtonsPrev[button];
	}

	public Vec2 getMousePosition () {
		return new Vec2(_mouseX, _mouseY);
	}

	public Vec2 getMouseDelta () {
		return new Vec2(_mouseDX, _mouseDY);
	}

	public Vec2 getMouseScroll () {
		return new Vec2(_scrollX, _scrollY);
	}
}
