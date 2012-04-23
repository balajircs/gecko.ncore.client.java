package no.gecko.ncore.client.core;

import java.io.File;

import org.apache.commons.io.FileUtils;

public class TestInitializer {

	private static boolean initialized = false;

	public static void init() throws Exception {

		if (!initialized) {

			String configData = FileUtils.readFileToString(new File(
					"resources/config/config.xml"));
			NCore.init(configData);
		}
	}

}
