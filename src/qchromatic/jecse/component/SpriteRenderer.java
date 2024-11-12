package qchromatic.jecse.component;

import qchromatic.jecse.core.Sprite;
import qchromatic.jecse.core.Component;

public class SpriteRenderer extends Component {
	public Sprite sprite;
	public int pixelsPerUnit = 100;

	public SpriteRenderer () {
		dependencies.add(Transform.class);
	}
}
