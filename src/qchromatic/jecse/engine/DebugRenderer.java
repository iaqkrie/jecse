package qchromatic.jecse.engine;

import qchromatic.jecse.common.Quaternion;
import qchromatic.jecse.common.Vec3;
import qchromatic.jecse.common.Vec4;
import qchromatic.jecse.component.Transform;

import java.util.ArrayList;
import java.util.List;

public final class DebugRenderer {
	private static final List<Line> LINES = new ArrayList<>();

	private DebugRenderer () { }

	public static void line (Vec3 start, Vec3 end, Vec4 color) {
		if (start == null || end == null || color == null) return;

		LINES.add(new Line(new Vec3(start), new Vec3(end), new Vec4(color)));
	}

	public static void axis (Transform transform, float length) {
		if (transform == null) return;

		Vec3 origin = transform.worldPosition();
		Quaternion rotation = transform.worldRotation();

		line(origin, origin.added(rotation.right().multiplied(length)), new Vec4(1f, 0f, 0f, 1f));
		line(origin, origin.added(rotation.up().multiplied(length)), new Vec4(0f, 1f, 0f, 1f));
		line(origin, origin.added(rotation.forward().multiplied(length)), new Vec4(0f, 0.4f, 1f, 1f));
	}

	public static void bounds (Vec3 center, Vec3 halfExtents, Vec4 color) {
		if (center == null || halfExtents == null || color == null) return;

		float minX = center.x - halfExtents.x;
		float minY = center.y - halfExtents.y;
		float minZ = center.z - halfExtents.z;
		float maxX = center.x + halfExtents.x;
		float maxY = center.y + halfExtents.y;
		float maxZ = center.z + halfExtents.z;

		Vec3 p000 = new Vec3(minX, minY, minZ);
		Vec3 p001 = new Vec3(minX, minY, maxZ);
		Vec3 p010 = new Vec3(minX, maxY, minZ);
		Vec3 p011 = new Vec3(minX, maxY, maxZ);
		Vec3 p100 = new Vec3(maxX, minY, minZ);
		Vec3 p101 = new Vec3(maxX, minY, maxZ);
		Vec3 p110 = new Vec3(maxX, maxY, minZ);
		Vec3 p111 = new Vec3(maxX, maxY, maxZ);

		line(p000, p001, color);
		line(p000, p010, color);
		line(p000, p100, color);
		line(p111, p110, color);
		line(p111, p101, color);
		line(p111, p011, color);
		line(p001, p011, color);
		line(p001, p101, color);
		line(p010, p011, color);
		line(p010, p110, color);
		line(p100, p101, color);
		line(p100, p110, color);
	}

	public static void clear () {
		LINES.clear();
	}

	public static List<Line> consumeLines () {
		List<Line> result = List.copyOf(LINES);
		LINES.clear();
		return result;
	}

	public static final class Line {
		public final Vec3 start;
		public final Vec3 end;
		public final Vec4 color;

		private Line (Vec3 start, Vec3 end, Vec4 color) {
			this.start = start;
			this.end = end;
			this.color = color;
		}
	}
}
