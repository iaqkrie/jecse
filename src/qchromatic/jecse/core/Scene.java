package qchromatic.jecse.core;

import qchromatic.jecse.entity.Camera;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene {
	private final Map<String, Entity> _entities;

	public Scene () {
		_entities = new HashMap<>();
	}

	public void addEntity (String name, Entity entity) {
		_entities.put(name, entity);
	}

	public Entity getEntityByName (String name) {
		return _entities.get(name);
	}

	public Entity[] getEntitiesWithComponent (Class<? extends Component> componentClass) {
		List<Entity> entities = new ArrayList<>();
		for (Entity entity : _entities.values()) {
			if (entity.containsComponent(componentClass))
				entities.add(entity);
		}

		return entities.toArray(new Entity[0]);
	}

	public Entity[] getEntitiesWithChildComponentsOf (Class<? extends Component> componentClass) {
		List<Entity> entities = new ArrayList<>();
		for (Entity entity : _entities.values()) {
			if (entity.containsChildComponentsOf(componentClass))
				entities.add(entity);
		}

		return entities.toArray(new Entity[0]);
	}

	public boolean hasEntityWithComponent (Class<? extends Component> componentClass) {
		for (Entity entity : _entities.values()) {
			if (entity.containsComponent(componentClass))
				return true;
		}

		return false;
	}

	public boolean hasEntityWithChildComponentsOf (Class<? extends Component> componentClass) {
		for (Entity entity : _entities.values()) {
			if (entity.containsChildComponentsOf(componentClass))
				return true;
		}

		return false;
	}

	public void removeEntity (String name) {
		_entities.remove(name);
	}
}
