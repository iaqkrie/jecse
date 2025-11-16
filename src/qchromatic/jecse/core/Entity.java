package qchromatic.jecse.core;

import java.lang.reflect.Array;
import java.util.*;

public class Entity {
	private final String _id;
	private final Map<Class<? extends Component>, Component> _components;

	public Entity () { this("0"); }
	public Entity (String id) {
		_id = id;
		_components = new HashMap<>();
	}

	public String id () { return _id; }

	public <T extends Component> Entity addComponent (T component) {
		component.entity = this;
		_components.put(component.getClass(), component);

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
		_components.remove(componentClass);
	}
}
