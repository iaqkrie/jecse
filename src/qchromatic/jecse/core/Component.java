package qchromatic.jecse.core;

import java.util.ArrayList;
import java.util.List;

public abstract class Component {
	public Entity entity;
	public final List<Class<? extends Component>> dependencies = new ArrayList<>();
}
