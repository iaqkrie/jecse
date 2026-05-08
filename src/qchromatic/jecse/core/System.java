package qchromatic.jecse.core;

import qchromatic.jecse.engine.Scene;

public abstract class System {
	protected Scene scene;
	private int _order;

	public System () { this(0); }

	protected System (int order) {
		_order = order;
	}

	public void init () { }
	public void start () { }
	public void loop (float dtime) { }
	public void stop () { }

	public void destroy () { }

	public void onEntityAdded (Entity entity) { }
	public void onEntityRemoved (Entity entity) { }

	public void onComponentAdded (Entity entity, Component component) { }
	public void onComponentRemoved (Entity entity, Component component) { }

	public int order () { return _order; }

	public System order (int order) {
		_order = order;
		return this;
	}

	public System scene (Scene scene) {
		if (scene == null) return this;

		this.scene = scene;
		return this;
	}
}
