package qchromatic.jecse.core;

import java.util.*;

public class Entity {
	private final String _id;
	private final Map<Class<? extends Component>, Component> _components;

	private EntityObserver _observer;

	public Entity () { this("0"); }
	public Entity (String id) {
		_id = id;
		_components = new HashMap<>();
	}

	public String id () { return _id; }

	public void observer (EntityObserver observer) { _observer = observer; }

	public <T extends Component> Entity addComponent (T component) {
		if (component == null) return this;
		if (_components.containsKey(component.getClass())) return this;

		component.entity = this;
		_components.put(component.getClass(), component);

		if (_observer != null)
			_observer.onComponentAdded(this, component);

		return this;
	}

	public Component[] getAllComponents () {
		return _components.values().toArray(new Component[0]);
	}

	public <T extends Component> T getComponent (Class<T> componentClass) {
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
		return _components.containsKey(componentClass);
	}

	public boolean hasComponentInheritance(Class<? extends Component> componentClass) {
		for (Component component : _components.values()) {
			if (componentClass.isAssignableFrom(component.getClass())) {
				return true;
			}
		}
		return false;
	}

	public void removeComponent (Class<? extends Component> componentClass) {
		Component component = _components.remove(componentClass);

		if (component == null) return;

		if (_observer != null)
			_observer.onComponentRemoved(this, component);

		if (component instanceof Disposable disposable)
			disposable.destroy();
	}
}
