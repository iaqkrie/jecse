package qchromatic.jecse.engine;

public final class Time {
	private static Time _current = new Time();

	private float _deltaTime;
	private float _unscaledDeltaTime;
	private float _fixedDeltaTime;
	private float _timeScale;
	private float _time;
	private float _unscaledTime;
	private long _frame;
	private float _lastTime;
	private boolean _started;

	public Time () {
		_deltaTime = 0f;
		_unscaledDeltaTime = 0f;
		_fixedDeltaTime = 1f / 60f;
		_timeScale = 1f;
		_time = 0f;
		_unscaledTime = 0f;
		_frame = 0;
		_lastTime = 0f;
		_started = false;
	}

	public static Time current () { return _current; }

	public static void use (Time time) {
		if (time == null)
			throw new IllegalArgumentException("Time cannot be null");

		_current = time;
	}

	public void configure (EngineConfig config) {
		if (config == null) return;

		_fixedDeltaTime = config.fixedDeltaTime();
		_timeScale = config.timeScale();
	}

	public void reset (float now) {
		_deltaTime = 0f;
		_unscaledDeltaTime = 0f;
		_time = 0f;
		_unscaledTime = 0f;
		_frame = 0;
		_lastTime = now;
		_started = true;
	}

	public void update (float now) {
		if (!_started) {
			reset(now);
			return;
		}

		_unscaledDeltaTime = Math.max(0f, now - _lastTime);
		_deltaTime = _unscaledDeltaTime * _timeScale;
		_unscaledTime += _unscaledDeltaTime;
		_time += _deltaTime;
		_lastTime = now;
		_frame++;
	}

	public float deltaTime () { return _deltaTime; }

	public float unscaledDeltaTime () { return _unscaledDeltaTime; }

	public float fixedDeltaTime () { return _fixedDeltaTime; }

	public Time fixedDeltaTime (float fixedDeltaTime) {
		if (fixedDeltaTime <= 0f)
			throw new IllegalArgumentException("Fixed delta time must be positive");

		_fixedDeltaTime = fixedDeltaTime;
		return this;
	}

	public float timeScale () { return _timeScale; }

	public Time timeScale (float timeScale) {
		if (timeScale < 0f)
			throw new IllegalArgumentException("Time scale cannot be negative");

		_timeScale = timeScale;
		return this;
	}

	public float time () { return _time; }

	public float unscaledTime () { return _unscaledTime; }

	public long frame () { return _frame; }
}
