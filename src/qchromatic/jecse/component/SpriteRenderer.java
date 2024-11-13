package qchromatic.jecse.component;

import qchromatic.jecse.core.Sprite;
import qchromatic.jecse.core.Component;

public class SpriteRenderer extends Component {
	private Sprite _sprite;
	private int _pixelsPerUnit = 100;

	public SpriteRenderer () {
		dependencies.add(Transform.class);
	}

	public Sprite sprite () { return _sprite; }
	public int pixelsPerUnit () { return _pixelsPerUnit; }

	public SpriteRenderer sprite (Sprite sprite) {
		_sprite = sprite;
		return this;
	}

	public SpriteRenderer pixelsPerUnit (int pixelsPerUnit) {
		_pixelsPerUnit = pixelsPerUnit;
		return this;
	}
}
