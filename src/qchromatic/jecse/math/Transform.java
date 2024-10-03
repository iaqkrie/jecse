package qchromatic.jecse.math;

public class Transform {
	public Vec2f position;
	public float rotation;
	public Vec2f scale;

	public Transform () { this(new Vec2f(), 0f, new Vec2f(1f)); }
	public Transform (Transform other) { this(other.position, other.rotation, other.scale); }
	public Transform (Vec2f position, float rotation, Vec2f scale) {
		this.position = new Vec2f(position);
		this.rotation = rotation;
		this.scale = new Vec2f(scale);
	}
}
