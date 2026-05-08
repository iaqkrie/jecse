package common.input;

import qchromatic.jecse.common.Vec2;
import qchromatic.jecse.engine.InputState;

public final class InputStateTest {
	public static void main (String[] args) {
		InputState input = new InputState();

		input.setMousePosition(100f, 50f);
		expectVec2("first mouse position delta", input.getMouseDelta(), 0f, 0f);
		expectVec2("first mouse position", input.getMousePosition(), 100f, 50f);

		input.setMousePosition(110f, 45f);
		input.setMousePosition(125f, 40f);
		expectVec2("accumulated mouse delta", input.getMouseDelta(), 25f, -10f);

		input.update();
		expectVec2("delta after update", input.getMouseDelta(), 0f, 0f);
		expectVec2("position after update", input.getMousePosition(), 125f, 40f);

		input.setMousePosition(130f, 35f);
		expectVec2("delta after preserved position", input.getMouseDelta(), 5f, -5f);

		input.resetMousePositionTracking();
		input.setMousePosition(400f, 300f);
		expectVec2("delta after re-enter", input.getMouseDelta(), 0f, 0f);

		input.setScrollOffset(0f, 1f);
		input.setScrollOffset(0f, 2f);
		expectVec2("accumulated scroll", input.getMouseScroll(), 0f, 3f);

		input.update();
		expectVec2("scroll after update", input.getMouseScroll(), 0f, 0f);
	}

	private static void expectVec2 (String label, Vec2 actual, float expectedX, float expectedY) {
		if (actual.x != expectedX || actual.y != expectedY) {
			throw new AssertionError(label + ": expected (" + expectedX + ", " + expectedY + "), got (" + actual.x + ", " + actual.y + ")");
		}
	}
}
