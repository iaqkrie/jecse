package qchromatic.jecse.core;

import qchromatic.jecse.engine.Scene;

public abstract class System {
	protected Scene scene;

	public void init () { }
	public void loop (float dtime) { }

	public void update () { }

	public System scene (Scene scene) {
		if (scene == null) return this;

		this.scene = scene;
		return this;
	}
}
