package qchromatic.jecse.system;

import qchromatic.jecse.component.Script;
import qchromatic.jecse.core.Entity;
import qchromatic.jecse.core.System;

import java.util.List;

public class ScriptSystem extends System {
	private List<Entity> _entities;

	@Override
	public void init () {
		updateEntitiesList();

		for (Entity entity : _entities) {
			List<Script> scripts = entity.getComponentsInheritance(Script.class);
			for (Script script : scripts)
				script.init();
		}
	}

	@Override
	public void loop (float dtime) {
		for (Entity entity : _entities) {
			List<Script> scripts = entity.getComponentsInheritance(Script.class);
			for (Script script : scripts)
				script.loop(dtime);
		}
	}

	@Override
	public void update () {
		updateEntitiesList();
	}

	private void updateEntitiesList () {
		_entities = List.of(scene.getEntitiesWithComponentInheritance(Script.class));
	}
}
