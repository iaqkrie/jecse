package qchromatic.jecse.engine;

import qchromatic.jecse.graphics.Window;

public final class EngineContext {
	private static EngineContext _current;

	private final InputState _input;
	private final AssetManager _assets;
	private final SceneManager _sceneManager;
	private final EngineConfig _config;
	private final Time _time;
	private final Window _window;

	public EngineContext () {
		this(new InputState(), new EngineConfig());
	}

	public EngineContext (InputState input) {
		this(input, new EngineConfig());
	}

	public EngineContext (InputState input, EngineConfig config) {
		this(input, new AssetManager(), new SceneManager(), config);
	}

	public EngineContext (Window window) {
		this(window, window.input(), new AssetManager(), new SceneManager(), new EngineConfig(), new Time());
	}

	public EngineContext (InputState input, AssetManager assets, SceneManager sceneManager) {
		this(input, assets, sceneManager, new EngineConfig());
	}

	public EngineContext (InputState input, AssetManager assets, SceneManager sceneManager, EngineConfig config) {
		this(new Window(input), input, assets, sceneManager, config, new Time());
	}

	public EngineContext (Window window, InputState input, AssetManager assets, SceneManager sceneManager) {
		this(window, input, assets, sceneManager, new EngineConfig(), new Time());
	}

	public EngineContext (Window window, InputState input, AssetManager assets, SceneManager sceneManager, EngineConfig config, Time time) {
		if (window == null)
			throw new IllegalArgumentException("Window cannot be null");
		if (input == null)
			throw new IllegalArgumentException("Input state cannot be null");
		if (assets == null)
			throw new IllegalArgumentException("Asset manager cannot be null");
		if (sceneManager == null)
			throw new IllegalArgumentException("Scene manager cannot be null");
		if (config == null)
			throw new IllegalArgumentException("Engine config cannot be null");
		if (time == null)
			throw new IllegalArgumentException("Time cannot be null");

		_window = window;
		_input = input;
		_assets = assets;
		_sceneManager = sceneManager;
		_config = config;
		_time = time;
		_time.configure(config);
	}

	public static EngineContext current () {
		if (_current == null)
			throw new RuntimeException("No current engine context");

		return _current;
	}

	public static EngineContext currentOrNull () { return _current; }

	public void use () {
		_current = this;
		Input.use(_input);
		Assets.use(_assets);
		Time.use(_time);
		_time.configure(_config);
	}

	public InputState input () { return _input; }

	public AssetManager assets () { return _assets; }

	public SceneManager sceneManager () { return _sceneManager; }

	public EngineConfig config () { return _config; }

	public Time time () { return _time; }

	public Window window () { return _window; }

	public void destroy () {
		_sceneManager.disposeActiveScene();
		_assets.destroy();
		_window.destroy();

		if (_current == this)
			_current = null;
	}
}
