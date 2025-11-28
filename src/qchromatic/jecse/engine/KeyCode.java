package qchromatic.jecse.engine;

import static org.lwjgl.glfw.GLFW.*;

public final class KeyCode {
	private KeyCode() { }

	// region KEYBOARD
	public static final int A = GLFW_KEY_A;
	public static final int B = GLFW_KEY_B;
	public static final int C = GLFW_KEY_C;
	public static final int D = GLFW_KEY_D;
	public static final int E = GLFW_KEY_E;
	public static final int F = GLFW_KEY_F;
	public static final int G = GLFW_KEY_G;
	public static final int H = GLFW_KEY_H;
	public static final int I = GLFW_KEY_I;
	public static final int J = GLFW_KEY_J;
	public static final int K = GLFW_KEY_K;
	public static final int L = GLFW_KEY_L;
	public static final int M = GLFW_KEY_M;
	public static final int N = GLFW_KEY_N;
	public static final int O = GLFW_KEY_O;
	public static final int P = GLFW_KEY_P;
	public static final int Q = GLFW_KEY_Q;
	public static final int R = GLFW_KEY_R;
	public static final int S = GLFW_KEY_S;
	public static final int T = GLFW_KEY_T;
	public static final int U = GLFW_KEY_U;
	public static final int V = GLFW_KEY_V;
	public static final int W = GLFW_KEY_W;
	public static final int X = GLFW_KEY_X;
	public static final int Y = GLFW_KEY_Y;
	public static final int Z = GLFW_KEY_Z;

	public static final int ALPHA_0 = GLFW_KEY_0;
	public static final int ALPHA_1 = GLFW_KEY_1;
	public static final int ALPHA_2 = GLFW_KEY_2;
	public static final int ALPHA_3 = GLFW_KEY_3;
	public static final int ALPHA_4 = GLFW_KEY_4;
	public static final int ALPHA_5 = GLFW_KEY_5;
	public static final int ALPHA_6 = GLFW_KEY_6;
	public static final int ALPHA_7 = GLFW_KEY_7;
	public static final int ALPHA_8 = GLFW_KEY_8;
	public static final int ALPHA_9 = GLFW_KEY_9;

	public static final int F1 = GLFW_KEY_F1;
	public static final int F2 = GLFW_KEY_F2;
	public static final int F3 = GLFW_KEY_F3;
	public static final int F4 = GLFW_KEY_F4;
	public static final int F5 = GLFW_KEY_F5;
	public static final int F6 = GLFW_KEY_F6;
	public static final int F7 = GLFW_KEY_F7;
	public static final int F8 = GLFW_KEY_F8;
	public static final int F9 = GLFW_KEY_F9;
	public static final int F10 = GLFW_KEY_F10;
	public static final int F11 = GLFW_KEY_F11;
	public static final int F12 = GLFW_KEY_F12;

	public static final int UP = GLFW_KEY_UP;
	public static final int DOWN = GLFW_KEY_DOWN;
	public static final int LEFT = GLFW_KEY_LEFT;
	public static final int RIGHT = GLFW_KEY_RIGHT;

	public static final int LEFT_SHIFT = GLFW_KEY_LEFT_SHIFT;
	public static final int RIGHT_SHIFT = GLFW_KEY_RIGHT_SHIFT;
	public static final int LEFT_CONTROL = GLFW_KEY_LEFT_CONTROL;
	public static final int RIGHT_CONTROL = GLFW_KEY_RIGHT_CONTROL;
	public static final int LEFT_ALT = GLFW_KEY_LEFT_ALT;
	public static final int RIGHT_ALT = GLFW_KEY_RIGHT_ALT;
	public static final int LEFT_SUPER = GLFW_KEY_LEFT_SUPER;
	public static final int RIGHT_SUPER = GLFW_KEY_RIGHT_SUPER;

	public static final int SPACE = GLFW_KEY_SPACE;
	public static final int ENTER = GLFW_KEY_ENTER;
	public static final int TAB = GLFW_KEY_TAB;
	public static final int BACKSPACE = GLFW_KEY_BACKSPACE;
	public static final int DELETE = GLFW_KEY_DELETE;
	public static final int ESCAPE = GLFW_KEY_ESCAPE;

	public static final int INSERT = GLFW_KEY_INSERT;
	public static final int HOME = GLFW_KEY_HOME;
	public static final int END = GLFW_KEY_END;
	public static final int PAGE_UP = GLFW_KEY_PAGE_UP;
	public static final int PAGE_DOWN = GLFW_KEY_PAGE_DOWN;

	public static final int CAPS_LOCK = GLFW_KEY_CAPS_LOCK;
	public static final int SCROLL_LOCK = GLFW_KEY_SCROLL_LOCK;
	public static final int NUM_LOCK = GLFW_KEY_NUM_LOCK;
	public static final int PRINT_SCREEN = GLFW_KEY_PRINT_SCREEN;
	public static final int PAUSE = GLFW_KEY_PAUSE;

	public static final int NUMPAD_0 = GLFW_KEY_KP_0;
	public static final int NUMPAD_1 = GLFW_KEY_KP_1;
	public static final int NUMPAD_2 = GLFW_KEY_KP_2;
	public static final int NUMPAD_3 = GLFW_KEY_KP_3;
	public static final int NUMPAD_4 = GLFW_KEY_KP_4;
	public static final int NUMPAD_5 = GLFW_KEY_KP_5;
	public static final int NUMPAD_6 = GLFW_KEY_KP_6;
	public static final int NUMPAD_7 = GLFW_KEY_KP_7;
	public static final int NUMPAD_8 = GLFW_KEY_KP_8;
	public static final int NUMPAD_9 = GLFW_KEY_KP_9;

	public static final int NUMPAD_DECIMAL = GLFW_KEY_KP_DECIMAL;
	public static final int NUMPAD_DIVIDE = GLFW_KEY_KP_DIVIDE;
	public static final int NUMPAD_MULTIPLY = GLFW_KEY_KP_MULTIPLY;
	public static final int NUMPAD_SUBTRACT = GLFW_KEY_KP_SUBTRACT;
	public static final int NUMPAD_ADD = GLFW_KEY_KP_ADD;
	public static final int NUMPAD_ENTER = GLFW_KEY_KP_ENTER;
	public static final int NUMPAD_EQUAL = GLFW_KEY_KP_EQUAL;

	public static final int MINUS = GLFW_KEY_MINUS;
	public static final int EQUAL = GLFW_KEY_EQUAL;
	public static final int LEFT_BRACKET = GLFW_KEY_LEFT_BRACKET;
	public static final int RIGHT_BRACKET = GLFW_KEY_RIGHT_BRACKET;
	public static final int BACKSLASH = GLFW_KEY_BACKSLASH;
	public static final int SEMICOLON = GLFW_KEY_SEMICOLON;
	public static final int APOSTROPHE = GLFW_KEY_APOSTROPHE;
	public static final int GRAVE_ACCENT = GLFW_KEY_GRAVE_ACCENT;
	public static final int COMMA = GLFW_KEY_COMMA;
	public static final int PERIOD = GLFW_KEY_PERIOD;
	public static final int SLASH = GLFW_KEY_SLASH;
	// endregion
	// region MOUSE
	public static final int MB_1 = GLFW_MOUSE_BUTTON_1;
	public static final int MB_2 = GLFW_MOUSE_BUTTON_2;
	public static final int MB_3 = GLFW_MOUSE_BUTTON_3;
	public static final int MB_4 = GLFW_MOUSE_BUTTON_4;
	public static final int MB_5 = GLFW_MOUSE_BUTTON_5;
	public static final int MB_6 = GLFW_MOUSE_BUTTON_6;
	public static final int MB_7 = GLFW_MOUSE_BUTTON_7;
	public static final int MB_8 = GLFW_MOUSE_BUTTON_8;
	// endregion
}
