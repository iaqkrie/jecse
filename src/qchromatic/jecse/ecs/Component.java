package qchromatic.jecse.ecs;

import java.util.ArrayList;
import java.util.List;

public abstract class Component {
	public Entity entity;
	public List<Class<? extends Component>> dependencies = new ArrayList<>();
}
