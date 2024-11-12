package qchromatic.jecse.system;

import qchromatic.jecse.component.Script;
import qchromatic.jecse.core.SceneManager;
import qchromatic.jecse.core.Entity;

public class ScriptSystem {
	public static void init () {
		if (SceneManager.getActiveScene() == null)
			throw new RuntimeException("No active scene!");

		Entity[] entities = SceneManager.getActiveScene().getEntitiesWithChildComponentsOf(Script.class);

		for (Entity entity : entities) {
			for (Script script : entity.getChildComponentsOf(Script.class))
				script.init.run();
		}
	}

	public static void loop (float dtime) {
		if (SceneManager.getActiveScene() == null)
			throw new RuntimeException("No active scene!");

		Entity[] entities = SceneManager.getActiveScene().getEntitiesWithChildComponentsOf(Script.class);

		for (Entity entity : entities) {
			for (Script script : entity.getChildComponentsOf(Script.class))
				script.loop.accept(dtime);
		}
	}
}
