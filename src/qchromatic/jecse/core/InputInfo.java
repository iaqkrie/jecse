package qchromatic.jecse.core;

import qchromatic.jecse.math.Vec2f;

import java.util.ArrayList;
import java.util.List;

public class InputInfo {
	public static List<Integer> pressedKeys = new ArrayList<>();
	public static List<Integer> pressedMouseButtons = new ArrayList<>();
	public static Vec2f mousePosition = new Vec2f();

	static List<Integer> wasPressedKeys = new ArrayList<>();
	static List<Integer> wasPressedMouseButtons = new ArrayList<>();

	public static void update () {
		wasPressedKeys.clear();
		wasPressedKeys.addAll(pressedKeys);

		wasPressedMouseButtons.clear();
		wasPressedMouseButtons.addAll(pressedMouseButtons);
	}
}
