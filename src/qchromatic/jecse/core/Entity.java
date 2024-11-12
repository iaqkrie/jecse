package qchromatic.jecse.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entity {
	private final Map<Class<? extends Component>, Component> _components;

	public Entity () {
		_components = new HashMap<>();
	}

	public <T extends Component> void addComponent (T component) {
		if (_components.containsKey(component.getClass()))
			return;

		for (Class<? extends Component> dependence : component.dependencies) {
			if (_components.containsKey(dependence))
				continue;

			try {
				addComponent(dependence.getConstructor().newInstance());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		component.entity = this;
		_components.put(component.getClass(), component);
	}

	public <T extends Component> T getComponent (Class<T> componentClass) {
		if (_components.containsKey(componentClass))
			return componentClass.cast(_components.get(componentClass));

		return null;
	}

	public <T extends Component> List<T> getChildComponentsOf (Class<T> componentClass) {
		List<T> components = new ArrayList<>();
		for (Component component : _components.values()) {
			if (componentClass.isAssignableFrom(component.getClass()))
				components.add(componentClass.cast(component));
		}

		return components;
	}

	public <T extends Component> void removeComponent (Class<T> componentClass) {
		_components.remove(componentClass);
	}

	public boolean containsComponent (Class<? extends Component> componentClass) {
		return _components.containsKey(componentClass);
	}

	public boolean containsChildComponentsOf (Class<? extends Component> componentClass) {
		for (Component component : _components.values()) {
			if (componentClass.isAssignableFrom(component.getClass()))
				return true;
		}

		return false;
	}
}
