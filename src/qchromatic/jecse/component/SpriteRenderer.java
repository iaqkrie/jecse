package qchromatic.jecse.component;

import qchromatic.jecse.core.Sprite;
import qchromatic.jecse.ecs.Component;

public class SpriteRenderer extends Component {
	public Sprite sprite;

	public SpriteRenderer () {
		dependencies.add(Transform.class);
	}
}
