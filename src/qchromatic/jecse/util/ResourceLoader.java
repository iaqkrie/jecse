package qchromatic.jecse.util;

import java.io.IOException;
import java.io.InputStream;

public class ResourceLoader {
	public static byte[] loadFromJar (String path) {
		try (InputStream is = ResourceLoader.class.getResourceAsStream(path)) {
			if (is == null)
				throw new RuntimeException("Cannot find resource!");

			return is.readAllBytes();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
