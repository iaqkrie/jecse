package qchromatic.jecse.script;

import qchromatic.jecse.common.Quaternion;
import qchromatic.jecse.common.Vec3;
import qchromatic.jecse.component.Script;
import qchromatic.jecse.component.Transform;
import qchromatic.jecse.engine.Input;
import qchromatic.jecse.engine.KeyCode;

public class FreecamController extends Script {
	private float _speed;
	private float _sensitivity;

	private Transform _transform;

	private float _pitch;
	private float _yaw;

	@Override
	public void init () {
		_transform = entity.getComponent(Transform.class);
	}

	@Override
	public void loop (float dtime) {
		if (_transform == null) return;

		move(dtime);
		look(dtime);
	}

	public float speed () { return _speed; }
	public FreecamController speed (float speed) {
		_speed = speed;
		return this;
	}

	public float sensitivity () { return _sensitivity; }
	public FreecamController sensitivity (float sensitivity) {
		_sensitivity = sensitivity;
		return this;
	}

	private void move (float dtime) {
		Vec3 right = _transform.rotation().right();
		Vec3 forward = _transform.rotation().forward();

		if (Input.getKey(KeyCode.W))
			_transform.position(_transform.position().added(forward.multiplied(_speed * dtime)));
		if (Input.getKey(KeyCode.A))
			_transform.position(_transform.position().added(right.multiplied(-_speed * dtime)));
		if (Input.getKey(KeyCode.S))
			_transform.position(_transform.position().added(forward.multiplied(-_speed * dtime)));
		if (Input.getKey(KeyCode.D))
			_transform.position(_transform.position().added(right.multiplied(_speed * dtime)));

		if (Input.getKey(KeyCode.SPACE))
			_transform.position(_transform.position().added(new Vec3(0f, _speed * dtime, 0f)));
		if (Input.getKey(KeyCode.LEFT_SHIFT))
			_transform.position(_transform.position().added(new Vec3(0f, -_speed * dtime, 0f)));
	}

	private void look (float dtime) {
		_pitch -= Input.getMouseDelta().y * dtime * _sensitivity;
		_yaw -= Input.getMouseDelta().x * dtime * _sensitivity;

		_transform.rotation(Quaternion.euler(_pitch, _yaw, 0f));
	}
}
