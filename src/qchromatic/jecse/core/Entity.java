package qchromatic.jecse.core;

import java.util.HashMap;
import java.util.Map;

public class Entity {
	private final Map<Class<? extends Component>, Component> _components;

	public Entity () {
		_components = new HashMap<>();
	}

	public <T extends Component> void addComponent (T component) {
		component.entity = this;
		_components.put(component.getClass(), component);
	}

	public <T extends Component> T getComponent (Class<T> componentClass) {
		return componentClass.cast(_components.get(componentClass));
	}

	public boolean hasComponent (Class<? extends Component> componentClass) {
		return _components.containsKey(componentClass);
	}

	public void removeComponent (Class<? extends Component> componentClass) {
		_components.remove(componentClass);
	}
}
