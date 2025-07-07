package qchromatic.jecse.common;

public class Vec3 {
	public int x;
	public int y;
	public int z;

	public Vec3 (Vec3 other) {
		if (other == null) return;

		x = other.x;
		y = other.y;
		z = other.z;
	}

	public Vec3 (Vec2 xy, int z) {
		if (xy == null) return;

		x = xy.x;
		y = xy.y;
		this.z = z;
	}

	public Vec3 () { this(0); }
	public Vec3 (int xyz) { this(xyz, xyz, xyz); }
	public Vec3 (int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vec3 add (Vec3 other) {
		if (other == null) return this;

		x += other.x;
		y += other.y;
		z += other.z;

		return this;
	}
}
