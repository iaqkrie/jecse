package qchromatic.jecse.engine;

import qchromatic.jecse.common.Vec4;

public final class EngineConfig {
	private Vec4 _clearColor;
	private float _fixedDeltaTime;
	private float _timeScale;
	private boolean _autoUpdateCameraAspect;
	private boolean _debugOpenGLErrors;

	public EngineConfig () {
		_clearColor = new Vec4(0.2f, 0.1f, 0.2f, 1f);
		_fixedDeltaTime = 1f / 60f;
		_timeScale = 1f;
		_autoUpdateCameraAspect = true;
		_debugOpenGLErrors = false;
	}

	public Vec4 clearColor () { return new Vec4(_clearColor); }
	public EngineConfig clearColor (Vec4 clearColor) {
		if (clearColor == null)
			throw new IllegalArgumentException("Clear color cannot be null");

		_clearColor = new Vec4(clearColor);
		return this;
	}
	public EngineConfig clearColor (float r, float g, float b, float a) {
		return clearColor(new Vec4(r, g, b, a));
	}

	public float fixedDeltaTime () { return _fixedDeltaTime; }
	public EngineConfig fixedDeltaTime (float fixedDeltaTime) {
		if (fixedDeltaTime <= 0f)
			throw new IllegalArgumentException("Fixed delta time must be positive");

		_fixedDeltaTime = fixedDeltaTime;
		return this;
	}

	public float timeScale () { return _timeScale; }
	public EngineConfig timeScale (float timeScale) {
		if (timeScale < 0f)
			throw new IllegalArgumentException("Time scale cannot be negative");

		_timeScale = timeScale;
		return this;
	}

	public boolean autoUpdateCameraAspect () { return _autoUpdateCameraAspect; }
	public EngineConfig autoUpdateCameraAspect (boolean autoUpdateCameraAspect) {
		_autoUpdateCameraAspect = autoUpdateCameraAspect;
		return this;
	}

	public boolean debugOpenGLErrors () { return _debugOpenGLErrors; }
	public EngineConfig debugOpenGLErrors (boolean debugOpenGLErrors) {
		_debugOpenGLErrors = debugOpenGLErrors;
		return this;
	}
}
