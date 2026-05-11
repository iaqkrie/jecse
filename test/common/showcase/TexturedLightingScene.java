package common.showcase;

import qchromatic.jecse.common.Quaternion;
import qchromatic.jecse.common.Vec3;
import qchromatic.jecse.common.Vec4;
import qchromatic.jecse.component.Camera;
import qchromatic.jecse.component.DirectionalLight;
import qchromatic.jecse.component.MeshRenderer;
import qchromatic.jecse.component.Script;
import qchromatic.jecse.component.Transform;
import qchromatic.jecse.core.Entity;
import qchromatic.jecse.engine.Application;
import qchromatic.jecse.engine.EngineConfig;
import qchromatic.jecse.engine.Material;
import qchromatic.jecse.engine.Meshes;
import qchromatic.jecse.engine.Scene;
import qchromatic.jecse.graphics.Texture;
import qchromatic.jecse.script.FreecamController;
import qchromatic.jecse.system.RenderSystem;
import qchromatic.jecse.system.ScriptSystem;

import java.util.List;

public final class TexturedLightingScene {
	private static final int LAYER_WORLD = 1;
	private static final int TEXTURE_SIZE = 16;

	public static void main (String[] args) {
		EngineConfig config = new EngineConfig()
				.clearColor(0.045f, 0.065f, 0.09f, 1f)
				.autoUpdateCameraAspect(true)
				.debugOpenGLErrors(false);

		Application app = new Application("jecse textured lighting", config);
		app.getWindow().setCursorDisabled();
		app.context().sceneManager().load(createScene());
		app.run();
	}

	private static Scene createScene () {
		Scene scene = new Scene();
		TexturePack textures = TexturePack.create();

		scene.addSystem(new ScriptSystem());
		scene.addSystem(new RenderSystem()
				.ambientColor(new Vec4(0.12f, 0.14f, 0.17f, 1f)));

		DirectionalLight sun = new DirectionalLight()
				.direction(-0.45f, -0.8f, -0.25f)
				.color(new Vec4(1f, 0.94f, 0.78f, 1f))
				.intensity(1.25f);

		scene.addEntity(new Entity("animated-sun")
				.addComponent(sun)
				.addComponent(new OrbitingLightScript(sun)));

		scene.addEntity(new Entity("camera")
				.addComponent(new Transform()
						.position(4f, 4f, 8f)
						.rotation(Quaternion.euler(-25f, 28f, 0f)))
				.addComponent(new Camera()
						.fov(58f)
						.near(0.1f)
						.far(80f)
						.cullingMask(LAYER_WORLD)
						.priority(10))
				.addComponent(new FreecamController()
						.speed(6f)
						.sensitivity(18f)));

		addBlockField(scene, textures);
		addDisplayBlocks(scene, textures);

		scene.addEntity(new Entity("texture-cleanup")
				.addComponent(new TextureCleanupScript(textures.all())));

		return scene;
	}

	private static void addBlockField (Scene scene, TexturePack textures) {
		for (int x = -4; x <= 4; x++) {
			for (int z = -4; z <= 4; z++) {
				Texture texture = (x + z) % 5 == 0 ? textures.dirt : textures.grass;
				float height = ((x * 31 + z * 17) & 3) == 0 ? 0.18f : 0f;
				addTexturedCube(scene, "terrain-" + x + "-" + z, x, height, z, texture, new Vec3(1f, 1f + height * 2f, 1f));
			}
		}

		addTexturedCube(scene, "stone-pillar-a", -2f, 1.2f, -1f, textures.stone, new Vec3(1f, 2.4f, 1f));
		addTexturedCube(scene, "stone-pillar-b", 2f, 0.8f, 1f, textures.stone, new Vec3(1f, 1.6f, 1f));
	}

	private static void addDisplayBlocks (Scene scene, TexturePack textures) {
		addTexturedCube(scene, "grass-sample", -3f, 1.45f, 3.2f, textures.grass, new Vec3(1f));
		addTexturedCube(scene, "dirt-sample", -1.6f, 1.45f, 3.2f, textures.dirt, new Vec3(1f));
		addTexturedCube(scene, "stone-sample", -0.2f, 1.45f, 3.2f, textures.stone, new Vec3(1f));
	}

	private static void addTexturedCube (Scene scene, String id, float x, float y, float z, Texture texture, Vec3 scale) {
		scene.addEntity(new Entity(id)
				.addComponent(new Transform()
						.position(x, y, z)
						.scale(scale))
				.addComponent(new MeshRenderer()
						.mesh(Meshes.cube())
						.material(new Material()
								.texture(texture)
								.color(new Vec4(1f, 1f, 1f, 1f)))
						.layer(LAYER_WORLD)
						.boundsRadius(Math.max(scale.x, Math.max(scale.y, scale.z)))));
	}

	private static Texture createTexture (PixelShader shader) {
		Texture texture = new Texture(TEXTURE_SIZE, TEXTURE_SIZE);
		for (int y = 0; y < TEXTURE_SIZE; y++) {
			for (int x = 0; x < TEXTURE_SIZE; x++) {
				Vec4 color = shader.color(x, y, noise(x, y));
				texture.setPixel(x, y, color.x, color.y, color.z, color.w);
			}
		}

		texture.uploadToGPU();
		return texture;
	}

	private static float noise (int x, int y) {
		int value = x * 73428767 ^ y * 91227153 ^ 0x45d9f3b;
		value = (value ^ (value >>> 16)) * 224682251;
		value = (value ^ (value >>> 13)) * 326648991;
		value = value ^ (value >>> 16);
		return (value & 255) / 255f;
	}

	private interface PixelShader {
		Vec4 color (int x, int y, float noise);
	}

	private static final class TexturePack {
		private final Texture grass;
		private final Texture dirt;
		private final Texture stone;

		private TexturePack (Texture grass, Texture dirt, Texture stone) {
			this.grass = grass;
			this.dirt = dirt;
			this.stone = stone;
		}

		private static TexturePack create () {
			Texture grass = createTexture((x, y, n) -> {
				float checker = (x + y) % 2 == 0 ? 0.04f : -0.02f;
				return new Vec4(0.24f + n * 0.08f + checker, 0.50f + n * 0.20f, 0.12f + n * 0.05f, 1f);
			});
			Texture dirt = createTexture((x, y, n) -> {
				float pebble = n > 0.76f ? 0.12f : 0f;
				return new Vec4(0.34f + n * 0.12f + pebble, 0.20f + n * 0.08f + pebble * 0.5f, 0.10f + n * 0.05f, 1f);
			});
			Texture stone = createTexture((x, y, n) -> {
				float seam = x == 0 || y == 0 || x == TEXTURE_SIZE - 1 || y == TEXTURE_SIZE - 1 ? -0.06f : 0f;
				float crack = ((x * 3 + y * 5) % 17 == 0) ? -0.18f : 0f;
				float value = 0.38f + n * 0.22f + seam + crack;
				return new Vec4(value, value, value * 1.04f, 1f);
			});

			return new TexturePack(grass, dirt, stone);
		}

		private List<Texture> all () {
			return List.of(grass, dirt, stone);
		}
	}

	private static final class OrbitingLightScript extends Script {
		private final DirectionalLight _light;
		private float _angle;

		private OrbitingLightScript (DirectionalLight light) {
			_light = light;
		}

		@Override
		public void loop (float dtime) {
			_angle += dtime * 45f;
			float radians = (float) Math.toRadians(_angle);
			float x = (float) Math.cos(radians) * 0.85f;
			float z = (float) Math.sin(radians) * 0.85f;

			_light.direction(x, -0.55f, z);

			float warmth = ((float) Math.sin(radians) + 1f) * 0.5f;
			_light.color(new Vec4(1f, 0.82f + warmth * 0.16f, 0.58f + warmth * 0.22f, 1f));
			_light.intensity(1.05f + warmth * 0.35f);
		}
	}

	private static final class TextureCleanupScript extends Script {
		private final List<Texture> _textures;

		private TextureCleanupScript (List<Texture> textures) {
			_textures = textures;
		}

		@Override
		public void destroy () {
			for (Texture texture : _textures)
				texture.destroy();
		}
	}
}
