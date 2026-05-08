package qchromatic.jecse.core;

import java.util.*;

public final class EntityQuery implements Iterable<Entity> {
    private final Set<Entity> _entities;
    private final List<Class<? extends Component>> _requiredComponents;
    private final boolean _inheritance;

    public EntityQuery (Collection<Class<? extends Component>> requiredComponents, boolean inheritance) {
        _entities = new LinkedHashSet<>();
        _requiredComponents = List.copyOf(requiredComponents);
        _inheritance = inheritance;
    }

    public List<Class<? extends Component>> requiredComponents () { return _requiredComponents; }

    public boolean inheritance () { return _inheritance; }

    public int size () { return _entities.size(); }

    public boolean isEmpty () { return _entities.isEmpty(); }

    public boolean contains (Entity entity) { return _entities.contains(entity); }

    public boolean matches (Entity entity) {
        if (entity == null) return false;

        for (Class<? extends Component> componentClass : _requiredComponents) {
            if (!(_inheritance ? entity.hasComponentInheritance(componentClass) : entity.hasComponent(componentClass)))
                return false;
        }

        return true;
    }

    public void update (Entity entity) {
        if (matches(entity))
            _entities.add(entity);
        else
            _entities.remove(entity);
    }

    public void add (Entity entity) {
        if (entity != null) _entities.add(entity);
    }

    public void remove (Entity entity) {
        _entities.remove(entity);
    }

    public void clear () {
        _entities.clear();
    }

    public List<Entity> snapshot () {
        return List.copyOf(_entities);
    }

    @Override
    public Iterator<Entity> iterator() {
        return snapshot().iterator();
    }
}
