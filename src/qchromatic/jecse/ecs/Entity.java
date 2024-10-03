package qchromatic.jecse.ecs;

import java.util.HashMap;
import java.util.Map;

public class Entity {
	private final Map<Class<? extends Component>, Component> _components;

	public Entity () {
		_components = new HashMap<>();
	}

	public <T extends Component> void addComponent (T component) {
		if (_components.containsKey(component.getClass()))
			return;

		_components.put(component.getClass(), component);
	}

	public <T extends Component> T getComponent (Class<T> componentClass) {
		if (_components.containsKey(componentClass))
			return componentClass.cast(_components.get(componentClass));

		return null;
	}

	public <T extends Component> void removeComponent (Class<T> componentClass) {
		_components.remove(componentClass);
	}

	public boolean containsComponent (Class<? extends Component> componentClass) {
		return _components.containsKey(componentClass);
	}
}
