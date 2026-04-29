package qchromatic.jecse.system;

import qchromatic.jecse.component.Script;
import qchromatic.jecse.core.Component;
import qchromatic.jecse.core.Entity;
import qchromatic.jecse.core.EntityQuery;
import qchromatic.jecse.core.System;

import java.util.List;

public class ScriptSystem extends System {
	private EntityQuery _scriptEntities;

	@Override
	public void init () {
		_scriptEntities = scene.queryInheritance(Script.class);

		for (Entity entity : _scriptEntities) {
			List<Script> scripts = entity.getComponentsInheritance(Script.class);

			for (Script script : scripts)
				script.init();
		}
	}

	@Override
	public void loop (float dtime) {
		for (Entity entity : _scriptEntities) {
			List<Script> scripts = entity.getComponentsInheritance(Script.class);

			for (Script script : scripts)
				script.loop(dtime);
		}
	}

	@Override
	public void onComponentAdded(Entity entity, Component component) {
		if (component instanceof Script script)
			script.init();
	}
}
