package qchromatic.jecse.engine;

import qchromatic.jecse.core.*;
import qchromatic.jecse.core.System;

import java.util.*;

public final class Scene implements EntityObserver {
	private final Map<String, Entity> _entities;
	private final Map<Class<? extends System>, System> _systemMap;
	private final List<System> _systems;
	private final List<EntityQuery> _queries;
	private final Map<Class<? extends Component>, Set<Entity>> _componentIndex;
	private final List<Runnable> _pendingStructuralChanges;

	private boolean _initialized;
	private boolean _running;
	private boolean _disposed;
	private boolean _updating;
	private boolean _flushingStructuralChanges;

	public Scene () {
		_entities = new LinkedHashMap<>();
		_systemMap = new HashMap<>();
		_systems = new ArrayList<>();
		_queries = new ArrayList<>();
		_componentIndex = new HashMap<>();
		_pendingStructuralChanges = new ArrayList<>();

		_initialized = false;
		_running = false;
		_disposed = false;
		_updating = false;
		_flushingStructuralChanges = false;
	}

	public void init () {
		ensureNotDisposed();
		initializeIfNeeded();
		start();
	}

	public void start () {
		ensureNotDisposed();
		initializeIfNeeded();

		if (_running) return;

		_running = true;
		for (Entity entity : entitySnapshot())
			entity.startComponents();

		for (System system : systemSnapshot())
			system.start();

		flushStructuralChanges();
	}

	public void loop (float dtime) {
		if (!_running) return;

		flushStructuralChanges();

		_updating = true;
		try {
			for (System system : systemSnapshot())
				system.loop(dtime);
		} finally {
			_updating = false;
		}

		flushStructuralChanges();
	}

	public void stop () {
		if (!_running) return;

		flushStructuralChanges();
		_running = false;

		for (System system : systemSnapshot())
			system.stop();

		for (Entity entity : entitySnapshot())
			entity.stopComponents();
	}

	public void destroy () {
		dispose();
	}

	public void dispose () {
		if (_disposed) return;

		stop();

		for (System system : systemSnapshot())
			system.destroy();

		for (Entity entity : entitySnapshot())
			disposeEntity(entity);

		_pendingStructuralChanges.clear();
		_componentIndex.clear();
		_queries.clear();
		_systems.clear();
		_systemMap.clear();
		_entities.clear();

		_initialized = false;
		_disposed = true;
	}

	public void clear () {
		ensureNotDisposed();
		if (_updating || _flushingStructuralChanges) {
			_pendingStructuralChanges.add(this::clearNow);
			return;
		}

		clearNow();
	}

	private void clearNow () {
		for (Entity entity : entitySnapshot())
			removeEntityNow(entity.id());
	}

	public boolean isInitialized () { return _initialized; }

	public boolean isRunning () { return _running; }

	public boolean isDisposed () { return _disposed; }

	public void addEntity (Entity entity) {
		ensureNotDisposed();
		if (entity == null)
			throw new IllegalArgumentException("Entity cannot be null");

		if (_updating || _flushingStructuralChanges) {
			_pendingStructuralChanges.add(() -> addEntityNow(entity));
			return;
		}

		addEntityNow(entity);
	}

	public boolean hasEntity (String id) {
		return _entities.containsKey(id);
	}

	public Entity getEntity (String id) {
		Entity entity = _entities.get(id);
		if (entity == null) throw new RuntimeException("Entity not found: " + id);
		return entity;
	}

	public Collection<Entity> entities () {
		return List.copyOf(_entities.values());
	}

	public void removeEntity (String id) {
		ensureNotDisposed();

		if (_updating || _flushingStructuralChanges) {
			_pendingStructuralChanges.add(() -> removeEntityNow(id));
			return;
		}

		removeEntityNow(id);
	}

	public void addSystem (System system) {
		ensureNotDisposed();
		if (system == null)
			throw new IllegalArgumentException("System cannot be null");

		if (_updating || _flushingStructuralChanges) {
			_pendingStructuralChanges.add(() -> addSystemNow(system));
			return;
		}

		addSystemNow(system);
	}

	public <T extends System> T getSystem (Class<T> systemClass) {
		return systemClass.cast(_systemMap.get(systemClass));
	}

	public void removeSystem (Class<? extends System> systemClass) {
		ensureNotDisposed();

		if (_updating || _flushingStructuralChanges) {
			_pendingStructuralChanges.add(() -> removeSystemNow(systemClass));
			return;
		}

		removeSystemNow(systemClass);
	}

	@Override
	public void onComponentAdded(Entity entity, Component component) {
		if (_disposed) return;

		if (_updating || _flushingStructuralChanges) {
			_pendingStructuralChanges.add(() -> componentAddedNow(entity, component));
			return;
		}

		componentAddedNow(entity, component);
	}

	@Override
	public void onComponentRemoved(Entity entity, Component component) {
		if (_disposed) return;

		if (_updating || _flushingStructuralChanges) {
			_pendingStructuralChanges.add(() -> componentRemovedNow(entity, component));
			return;
		}

		componentRemovedNow(entity, component);
	}

	@SafeVarargs
	@SuppressWarnings("varargs")
	public final EntityQuery query (Class<? extends Component>... componentClasses) {
		return createQuery(false, Arrays.asList(componentClasses));
	}

	@SafeVarargs
	@SuppressWarnings("varargs")
	public final EntityQuery queryInheritance (Class<? extends Component>... componentClasses) {
		return createQuery(true, Arrays.asList(componentClasses));
	}

	private void initializeIfNeeded () {
		if (_initialized) return;

		for (Entity entity : _entities.values()) {
			entity.observer(this);
			indexEntity(entity);
		}

		for (EntityQuery query : _queries)
			rebuildQuery(query);

		for (System system : systemSnapshot())
			system.init();

		_initialized = true;
	}

	private void addEntityNow (Entity entity) {
		if (_entities.containsKey(entity.id()))
			throw new IllegalStateException("Scene already contains entity: " + entity.id());

		_entities.put(entity.id(), entity);
		entity.attachAllComponents();
		entity.observer(this);
		indexEntity(entity);
		updateQueries(entity);

		if (_initialized) {
			if (_running)
				entity.startComponents();

			for (System system : systemSnapshot())
				system.onEntityAdded(entity);
		}
	}

	private void removeEntityNow (String id) {
		Entity entity = _entities.remove(id);
		if (entity == null) return;

		for (EntityQuery query : _queries)
			query.remove(entity);

		unindexEntity(entity);

		if (_initialized) {
			for (System system : systemSnapshot())
				system.onEntityRemoved(entity);
		}

		disposeEntity(entity);
	}

	private void addSystemNow (System system) {
		if (_systemMap.containsKey(system.getClass()))
			throw new IllegalStateException("Scene already contains system: " + system.getClass().getName());

		_systemMap.put(system.getClass(), system.scene(this));
		_systems.add(system);
		_systems.sort(Comparator.comparingInt(System::order));

		if (_initialized) {
			system.init();
			if (_running)
				system.start();
		}
	}

	private void removeSystemNow (Class<? extends System> systemClass) {
		System system = _systemMap.remove(systemClass);
		if (system == null) return;

		_systems.remove(system);

		if (_running)
			system.stop();
		if (_initialized)
			system.destroy();
	}

	private void componentAddedNow (Entity entity, Component component) {
		if (!_entities.containsKey(entity.id())) return;

		indexComponent(entity, component);
		updateQueries(entity);

		if (_initialized) {
			if (_running)
				entity.startComponent(component);

			for (System system : systemSnapshot())
				system.onComponentAdded(entity, component);
		}
	}

	private void componentRemovedNow (Entity entity, Component component) {
		removeIndexedComponent(entity, component);
		updateQueries(entity);

		if (_initialized) {
			for (System system : systemSnapshot())
				system.onComponentRemoved(entity, component);
		}

		entity.finishComponentRemoval(component);
	}

	private EntityQuery createQuery (boolean inheritance, Collection<Class<? extends Component>> componentClasses) {
		LinkedHashSet<Class<? extends Component>> requiredComponents = new LinkedHashSet<>();
		for (Class<? extends Component> componentClass : componentClasses) {
			if (componentClass == null)
				throw new IllegalArgumentException("Query component class cannot be null");
			requiredComponents.add(componentClass);
		}

		EntityQuery query = new EntityQuery(requiredComponents, inheritance);
		rebuildQuery(query);
		_queries.add(query);
		return query;
	}

	private void rebuildQuery (EntityQuery query) {
		query.clear();
		for (Entity entity : candidatesFor(query)) {
			if (query.matches(entity))
				query.add(entity);
		}
	}

	private Collection<Entity> candidatesFor (EntityQuery query) {
		if (query.requiredComponents().isEmpty())
			return _entities.values();

		Set<Entity> result = null;
		for (Class<? extends Component> componentClass : query.requiredComponents()) {
			Set<Entity> candidates = query.inheritance()
					? indexedEntitiesAssignableTo(componentClass)
					: _componentIndex.getOrDefault(componentClass, Collections.emptySet());

			if (result == null)
				result = new LinkedHashSet<>(candidates);
			else
				result.retainAll(candidates);

			if (result.isEmpty())
				break;
		}

		return result == null ? _entities.values() : result;
	}

	private Set<Entity> indexedEntitiesAssignableTo (Class<? extends Component> componentClass) {
		Set<Entity> result = new LinkedHashSet<>();
		for (Map.Entry<Class<? extends Component>, Set<Entity>> entry : _componentIndex.entrySet()) {
			if (componentClass.isAssignableFrom(entry.getKey()))
				result.addAll(entry.getValue());
		}
		return result;
	}

	private void updateQueries (Entity entity) {
		for (EntityQuery query: _queries)
			query.update(entity);
	}

	private void indexEntity (Entity entity) {
		for (Component component : entity.components())
			indexComponent(entity, component);
	}

	private void unindexEntity (Entity entity) {
		for (Component component : entity.components())
			removeIndexedComponent(entity, component);
	}

	private void indexComponent (Entity entity, Component component) {
		_componentIndex
				.computeIfAbsent(component.getClass(), key -> new LinkedHashSet<>())
				.add(entity);
	}

	private void removeIndexedComponent (Entity entity, Component component) {
		Set<Entity> entities = _componentIndex.get(component.getClass());
		if (entities == null) return;

		entities.remove(entity);
		if (entities.isEmpty())
			_componentIndex.remove(component.getClass());
	}

	private void disposeEntity (Entity entity) {
		entity.destroyComponents();
		entity.detachAllComponents();
		entity.observer(null);
	}

	private void flushStructuralChanges () {
		if (_flushingStructuralChanges || _pendingStructuralChanges.isEmpty()) return;

		_flushingStructuralChanges = true;
		try {
			while (!_pendingStructuralChanges.isEmpty()) {
				List<Runnable> changes = List.copyOf(_pendingStructuralChanges);
				_pendingStructuralChanges.clear();

				for (Runnable change : changes)
					change.run();
			}
		} finally {
			_flushingStructuralChanges = false;
		}
	}

	private List<System> systemSnapshot () {
		return List.copyOf(_systems);
	}

	private List<Entity> entitySnapshot () {
		return List.copyOf(_entities.values());
	}

	private void ensureNotDisposed () {
		if (_disposed)
			throw new IllegalStateException("Scene is disposed");
	}
}
