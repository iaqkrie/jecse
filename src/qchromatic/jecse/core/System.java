package qchromatic.jecse.core;

import qchromatic.jecse.engine.Scene;

public abstract class System {
	protected Scene scene;

	public void init () { }
	public void loop (float dtime) { }

	public void destroy () { }

	public void onEntityAdded (Entity entity) { }
	public void onEntityRemoved (Entity entity) { }

	public void onComponentAdded (Entity entity, Component component) { }
	public void onComponentRemoved (Entity entity, Component component) { }

	public System scene (Scene scene) {
		if (scene == null) return this;

		this.scene = scene;
		return this;
	}
}
