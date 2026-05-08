package qchromatic.jecse.core;

public abstract class Component {
	protected Entity entity;
	private boolean _started;
	private boolean _destroyed;

	public Entity entity () { return entity; }

	public boolean started () { return _started; }

	public boolean destroyed () { return _destroyed; }

	public void onAttach () { }

	public void onDetach () { }

	public void onStart () { }

	public void onStop () { }

	public void onDestroy () { }

	final void attach (Entity entity) {
		if (this.entity == entity)
			return;

		if (this.entity != null && this.entity != entity)
			throw new IllegalStateException("Component is already attached to another entity");

		this.entity = entity;
		_destroyed = false;
		onAttach();
	}

	final void detach (Entity entity) {
		if (this.entity == entity) {
			stopInternal();
			onDetach();
			this.entity = null;
		}
	}

	final void startInternal () {
		if (_started || _destroyed) return;

		_started = true;
		onStart();
	}

	final void stopInternal () {
		if (!_started) return;

		_started = false;
		onStop();
	}

	final void destroyInternal () {
		if (_destroyed) return;

		stopInternal();
		_destroyed = true;
		onDestroy();

		if (this instanceof Disposable disposable)
			disposable.destroy();
	}
}
