package qchromatic.jecse.engine;

import qchromatic.jecse.core.*;
import qchromatic.jecse.core.System;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Scene implements EntityObserver {
	private final Map<String, Entity> _entities;
	private final Map<Class<? extends System>, System> _systems;

	private List<EntityQuery> _queries;

	private boolean _isActive;

	public Scene () {
		_entities = new HashMap<>();
		_systems = new HashMap<>();

		_queries = new ArrayList<>();

		_isActive = false;
	}

	public void init () {
		_isActive = true;

		for (Entity entity : _entities.values())
			entity.observer(this);

		for (System system : _systems.values())
			system.init();
	}

	public void loop (float dtime) {
		for (System system : _systems.values())
			system.loop(dtime);
	}

	public void destroy () {
		_isActive = false;

		for (System system : _systems.values())
			system.destroy();

		for (Entity entity : _entities.values()) {
			for (Component component : entity.getAllComponents()) {
				if (component instanceof Disposable disposable)
					disposable.destroy();
			}

			entity.observer(null);
		}

		_queries.clear();
		_systems.clear();
		_entities.clear();
	}

	public void addEntity (Entity entity) {
		if (entity == null || _entities.containsKey(entity.id())) return;

		_entities.put(entity.id(), entity);

		if (!_isActive) return;

		entity.observer(this);
		updateQueries(entity);

		for (System system : _systems.values())
			system.onEntityAdded(entity);
	}

	public Entity getEntity (String id) {
		if (!_entities.containsKey(id)) throw new RuntimeException("Entity not found");
		return _entities.get(id);
	}

	public void removeEntity (String id) {
		Entity entity = _entities.remove(id);

		if (entity == null) return;

		if (_isActive) {
			for (EntityQuery query : _queries)
				query.remove(entity);

			for (System system : _systems.values())
				system.onEntityRemoved(entity);
		}

		for (Component component : entity.getAllComponents()) {
			if (component instanceof Disposable disposable)
				disposable.destroy();
		}

		entity.observer(null);
	}

	public void addSystem (System system) {
		if (_isActive) throw new RuntimeException("Cannot add system to active scene");

		if (system == null || _systems.containsKey(system.getClass())) return;
		_systems.put(system.getClass(), system.scene(this));
	}

	@Override
	public void onComponentAdded(Entity entity, Component component) {
		if (!_isActive) return;

		updateQueries(entity);

		for (System system : _systems.values())
			system.onComponentAdded(entity, component);
	}

	@Override
	public void onComponentRemoved(Entity entity, Component component) {
		if (!_isActive) return;

		updateQueries(entity);

		for (System system : _systems.values())
			system.onComponentRemoved(entity, component);
	}

	public EntityQuery query (Class<? extends Component>... componentClasses) {
		return createQuery(false, componentClasses);
	}

	public EntityQuery queryInheritance (Class<? extends Component>... componentClasses) {
		return createQuery(true, componentClasses);
	}

	private EntityQuery createQuery (boolean inheritance, Class<? extends Component>... componentClasses) {
		EntityQuery query = new EntityQuery(componentClasses, inheritance);

		for (Entity entity: _entities.values())
			query.update(entity);

		_queries.add(query);
		return query;
	}

	private void updateQueries (Entity entity) {
		for (EntityQuery query: _queries)
			query.update(entity);
	}
}
