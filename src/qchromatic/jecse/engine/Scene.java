package qchromatic.jecse.engine;

import qchromatic.jecse.core.Component;
import qchromatic.jecse.core.Entity;
import qchromatic.jecse.core.System;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {
	private Map<String, Entity> _entities;
	private List<System> _systems;

	public Scene () {
		_entities = new HashMap<>();
		_systems = new ArrayList<>();
	}

	public void init () {
		for (System system : _systems)
			system.init();
	}

	public void loop (float dtime) {
		for (System system : _systems)
			system.loop(dtime);
	}

	public void addEntity (String id, Entity entity) {
		if (_entities.containsKey(id)) return;

		_entities.put(id, entity);
	}

	public Entity getEntity (String id) {
		if (!_entities.containsKey(id)) throw new RuntimeException("Entity not found");

		return _entities.get(id);
	}

	public Entity[] getEnitiesWithComponent (Class<? extends Component> componentClass) {
		List<Entity> entities = new ArrayList<>();
		for (Entity entity : _entities.values()) {
			if (entity.hasComponent(componentClass))
				entities.add(entity);
		}

		return entities.toArray(new Entity[0]);
	}

	public void addSystem (System system) {
		_systems.add(system);
	}
}
