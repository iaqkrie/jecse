package qchromatic.jecse.graphics;

@FunctionalInterface
public interface WindowResizeListener {
	void onResize (Window window, int width, int height);
}
