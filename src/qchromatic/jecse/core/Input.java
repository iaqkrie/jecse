package qchromatic.jecse.core;

import qchromatic.jecse.math.Vec2f;

public class Input {
	public static boolean getKey (int key) {
		return InputInfo.pressedKeys.contains(key);
	}

	public static boolean getKeyDown (int key) {
		return InputInfo.pressedKeys.contains(key) && !InputInfo.wasPressedKeys.contains(key);
	}

	public static boolean getKeyUp (int key) {
		return !InputInfo.pressedKeys.contains(key) && InputInfo.wasPressedKeys.contains(key);
	}

	public static boolean getMouseButton (int button) {
		return InputInfo.pressedMouseButtons.contains(button);
	}

	public static boolean getMouseButtonDown (int button) {
		return InputInfo.pressedMouseButtons.contains(button) && !InputInfo.wasPressedMouseButtons.contains(button);
	}

	public static boolean getMouseButtonUp (int button) {
		return !InputInfo.pressedMouseButtons.contains(button) && InputInfo.wasPressedMouseButtons.contains(button);
	}

	public static Vec2f getMousePosition () {
		return InputInfo.mousePosition;
	}
}
