package qchromatic.jecse.core;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public final class EntityQuery implements Iterable<Entity> {
    private final Set<Entity> _entities;
    private final Class<? extends Component>[] _requiredComponents;
    private final boolean _inheritance;

    public EntityQuery (Class<? extends Component>[] requiredComponents, boolean inheritance) {
        _entities = new LinkedHashSet<>();
        _requiredComponents = requiredComponents;
        _inheritance = inheritance;
    }

    public boolean matches (Entity entity) {
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

    public void remove (Entity entity) {
        _entities.remove(entity);
    }

    @Override
    public Iterator<Entity> iterator() {
        return _entities.iterator();
    }
}
