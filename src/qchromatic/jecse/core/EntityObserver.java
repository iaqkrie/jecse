package qchromatic.jecse.core;

public interface EntityObserver {
    void onComponentAdded (Entity entity, Component component);
    void onComponentRemoved (Entity entity, Component component);
}
