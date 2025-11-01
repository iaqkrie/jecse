package qchromatic.jecse.engine;

import qchromatic.jecse.core.Entity;
import qchromatic.jecse.core.System;

import java.util.HashMap;
import java.util.Map;

public class Scene {
	private Map<String, Entity> _entities;
	private Map<Class<? extends System>, System> _systems;

	public Scene () {
		_entities = new HashMap<>();
		_systems = new HashMap<>();
	}

	public void init () {
		for (System system : _systems.values())
			system.init();
	}

	public void loop (float dtime) {
		for (System system : _systems.values())
			system.loop(dtime);
	}

	public void addEntity (String id, Entity entity) {
		if (entity == null || _entities.containsKey(id)) return;
		_entities.put(id, entity);
	}

	public void addSystem (System system) {
		if (system == null || _systems.containsKey(system.getClass())) return;
		_systems.put(system.getClass(), system);
	}
}
