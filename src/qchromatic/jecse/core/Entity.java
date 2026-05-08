package qchromatic.jecse.core;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class Entity {
	private static final AtomicLong NEXT_ID = new AtomicLong(1);

	private final String _id;
	private final Map<Class<? extends Component>, Component> _components;

	private EntityObserver _observer;

	public Entity () { this("entity-" + NEXT_ID.getAndIncrement()); }
	public Entity (String id) {
		if (id == null || id.isBlank())
			throw new IllegalArgumentException("Entity id cannot be null or blank");

		_id = id;
		_components = new LinkedHashMap<>();
	}

	public String id () { return _id; }

	public void observer (EntityObserver observer) { _observer = observer; }

	public <T extends Component> Entity addComponent (T component) {
		if (component == null)
			throw new IllegalArgumentException("Component cannot be null");

		if (component.entity != null && component.entity != this)
			throw new IllegalStateException("Component is already attached to another entity");

		if (_components.containsKey(component.getClass()))
			throw new IllegalStateException("Entity already has component: " + component.getClass().getName());

		component.attach(this);
		_components.put(component.getClass(), component);

		if (_observer != null)
			_observer.onComponentAdded(this, component);

		return this;
	}

	public Component[] getAllComponents () {
		return _components.values().toArray(new Component[0]);
	}

	public Collection<Component> components () {
		return Collections.unmodifiableCollection(_components.values());
	}

	public void detachAllComponents () {
		for (Component component : _components.values())
			component.detach(this);
	}

	public void attachAllComponents () {
		for (Component component : _components.values())
			component.attach(this);
	}

	public void startComponents () {
		for (Component component : _components.values())
			component.startInternal();
	}

	public void stopComponents () {
		for (Component component : _components.values())
			component.stopInternal();
	}

	public void destroyComponents () {
		for (Component component : _components.values())
			component.destroyInternal();
	}

	public void startComponent (Component component) {
		if (component != null && _components.containsValue(component))
			component.startInternal();
	}

	public <T extends Component> T getComponent (Class<T> componentClass) {
		if (componentClass == null) return null;
		return componentClass.cast(_components.get(componentClass));
	}

	public <T extends Component> List<T> getComponentsInheritance(Class<T> componentClass) {
		List<T> result = new ArrayList<>();
		for (Component component : _components.values()) {
			if (componentClass.isAssignableFrom(component.getClass())) {
				result.add(componentClass.cast(component));
			}
		}

		return result;
	}

	public boolean hasComponent (Class<? extends Component> componentClass) {
		if (componentClass == null) return false;
		return _components.containsKey(componentClass);
	}

	public boolean hasComponentInheritance(Class<? extends Component> componentClass) {
		if (componentClass == null) return false;

		for (Component component : _components.values()) {
			if (componentClass.isAssignableFrom(component.getClass())) {
				return true;
			}
		}
		return false;
	}

	public void removeComponent (Class<? extends Component> componentClass) {
		if (componentClass == null) return;

		Component component = _components.remove(componentClass);

		if (component == null) return;

		if (_observer != null)
			_observer.onComponentRemoved(this, component);
		else
			finishComponentRemoval(component);
	}

	public void finishComponentRemoval (Component component) {
		if (component == null) return;
		component.destroyInternal();
		component.detach(this);
	}
}
