package no.gecko.ncore.client.core;

import java.util.List;
import java.util.UUID;

import no.gecko.ephorte.services.functions.v2.FunctionDescriptor;
import no.gecko.ephorte.services.functions.v2.FunctionResult;
import no.gecko.ephorte.services.objectmodel.v3.en.DataObjectT;
import no.gecko.ncore.client.config.ClientConfig;

public class NCore {

	private static boolean initialized;

	public static void init(String configData) {

		if (!initialized) {
			// initialize the client configuration
			ClientConfig.init(configData);

			// initialize the client pool
			ClientPool.init();

			initialized = true;
		}
	}

	public static class Objects {

		public static void insert(DataObjectT... dataObjects)
				throws Exception {

			NCoreClient client = null;
			try {
				client = ClientPool.get().borrowObject();
				client.insert(dataObjects);
			} finally {
				if (client != null) {
					ClientPool.get().returnObject(client);
				}
			}
		}

		public static void update(DataObjectT... dataObjects)
				throws Exception {

			NCoreClient client = null;
			try {
				client = ClientPool.get().borrowObject();
				client.update(dataObjects);
			} finally {
				if (client != null) {
					ClientPool.get().returnObject(client);
				}
			}
		}

		public static List<DataObjectT> filteredQuery(String objectName,
				String filterExpression, String[] relatedObjects,
				Integer skipCount, Integer takeCount) throws Exception {

			NCoreClient client = null;
			try {
				client = ClientPool.get().borrowObject();
				return client.filteredQuery(objectName, filterExpression,
						relatedObjects, skipCount, takeCount);
			} finally {
				if (client != null) {
					ClientPool.get().returnObject(client);
				}
			}
		}

	}

	public static class Documents {

		public static String uploadFile(String fileName,
				String storageIdentifier, byte[] contents) throws Exception {

			NCoreClient client = null;
			try {
				client = ClientPool.get().borrowObject();
				return client.uploadFile(fileName, storageIdentifier, contents);
			} finally {
				if (client != null) {
					ClientPool.get().returnObject(client);
				}
			}
		}

		public static void checkin(Integer documentDescriptionId,
				String variantFormat, Integer version, UUID uuid, String path,
				byte[] contents) throws Exception {

			NCoreClient client = null;
			try {
				client = ClientPool.get().borrowObject();
				client.checkin(documentDescriptionId, variantFormat, version,
						uuid, path, contents);
			} finally {
				if (client != null) {
					ClientPool.get().returnObject(client);
				}
			}
		}

		public static byte[] checkout(Integer documentDescriptionId,
				Integer version, String variantFormat) throws Exception {

			NCoreClient client = null;
			try {
				client = ClientPool.get().borrowObject();
				return client.checkout(documentDescriptionId, version,
						variantFormat);
			} finally {
				if (client != null) {
					ClientPool.get().returnObject(client);
				}
			}
		}

		public static byte[] getDocumentContentByRegistryEntryId(int journalpostId) throws Exception {
			NCoreClient client = null;
			try {
				client = ClientPool.get().borrowObject();
				return client.getDocumentContentByRegistryEntryId(journalpostId);
			} finally {
				if (client != null) {
					ClientPool.get().returnObject(client);
				}
			}
		}
		
		public static byte[] getDocumentContent(int documentId, int version, String variant) throws Exception{
			NCoreClient client = null;
			try {
				client = ClientPool.get().borrowObject();
				return client.getDocumentContent(documentId, version, variant);
			} finally {
				if (client != null) {
					ClientPool.get().returnObject(client);
				}
			}
		}
	}

	public static class Functions {

		public static List<FunctionDescriptor> queryFunctionDescriptors()
				throws Exception {

			NCoreClient client = null;
			try {
				client = ClientPool.get().borrowObject();
				return client.queryFunctionDescriptors();
			} finally {
				if (client != null) {
					ClientPool.get().returnObject(client);
				}
			}
		}

		public static FunctionResult executeFunction(String name,
				Object... params) throws Exception {

			NCoreClient client = null;
			try {
				client = ClientPool.get().borrowObject();
				return client.executeFunction(name, params);
			} finally {
				if (client != null) {
					ClientPool.get().returnObject(client);
				}
			}

		}

	}

}
