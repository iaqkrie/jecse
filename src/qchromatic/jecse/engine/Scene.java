package qchromatic.jecse.engine;

import qchromatic.jecse.core.Component;
import qchromatic.jecse.core.Entity;
import qchromatic.jecse.core.System;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Scene {
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

		for (System system : _systems.values())
			system.update();
	}

	public Entity getEntity (String id) {
		if (!_entities.containsKey(id)) throw new RuntimeException("Entity not found");
		return _entities.get(id);
	}

	public Entity[] getEntitiesWithComponent (Class<? extends Component> componentClass) {
		List<Entity> entities = new ArrayList<>();

		for (Entity entity : _entities.values()) {
			if (entity.hasComponent(componentClass))
				entities.add(entity);
		}

		return entities.toArray(new Entity[0]);
	}

	@SafeVarargs
	public final Entity[] getEntitiesWithComponents (Class<? extends Component>... componentClasses) {
		List<Entity> entities = new ArrayList<>();

		for (Entity entity: _entities.values()) {
			boolean containsAllComponents = true;
			for (Class<? extends Component> componentClass : componentClasses) {
				if (!entity.hasComponent(componentClass)) {
					containsAllComponents = false;
					break;
				}
			}

			if (containsAllComponents)
				entities.add(entity);
		}

		return entities.toArray(new Entity[0]);
	}

	public void addSystem (System system) {
		if (system == null || _systems.containsKey(system.getClass())) return;
		_systems.put(system.getClass(), system.scene(this));
	}
}
