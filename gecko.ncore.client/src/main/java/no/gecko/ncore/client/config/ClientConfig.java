package no.gecko.ncore.client.config;

import java.io.File;
import java.util.UUID;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.xpath.XPathExpressionEngine;
import org.apache.commons.io.FileUtils;

public class ClientConfig {

	private static XMLConfiguration config;

	private static String username;
	private static String password;
	private static String database;

	private static ServiceConfig objectModelServiceCfg;
	private static ServiceConfig documentServiceCfg;
	private static ServiceConfig functionsServiceCfg;

	private static int clientPoolSize;

	public static void init(String configData) {

		try {
			File configFile = File.createTempFile(UUID.randomUUID().toString(),
					".tmp");

			FileUtils.writeStringToFile(configFile, configData);

			config = new XMLConfiguration(configFile);
			config.setExpressionEngine(new XPathExpressionEngine());

			objectModelServiceCfg = getServiceConfig("objectModelService");
			documentServiceCfg = getServiceConfig("documentService");
			functionsServiceCfg = getServiceConfig("functionsService");

			username = config.getString("username");
			password = config.getString("password");
			database = config.getString("database");

			clientPoolSize = Integer.parseInt(config
					.getString("clientPoolSize"));

			// delete the temporary copy of the configuration
			configFile.deleteOnExit();

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private static ServiceConfig getServiceConfig(String id) {

		String address = config.getString("services/service[@id='" + id
				+ "']/address");
		String bindingStr = config.getString("services/service[@id='" + id
				+ "']/binding");

		ServiceConfig serviceConfig = new ServiceConfig(address, bindingStr);

		return serviceConfig;
	}

	public static String getUsername() {
		return username;
	}

	public static String getPassword() {
		return password;
	}

	public static String getDatabase() {
		return database;
	}

	public static ServiceConfig getObjectModelService() {
		return objectModelServiceCfg;
	}

	public static ServiceConfig getDocumentService() {
		return documentServiceCfg;
	}

	public static ServiceConfig getFunctionsService() {
		return functionsServiceCfg;
	}

	public static int getClientPoolSize() {
		return clientPoolSize;
	}

}
