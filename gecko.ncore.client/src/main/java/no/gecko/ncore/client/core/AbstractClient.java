package no.gecko.ncore.client.core;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.ws.BindingProvider;

import no.gecko.ephorte.services.documents.v3.DocumentIdentity;
import no.gecko.ephorte.services.documents.v3.DocumentService;
import no.gecko.ephorte.services.documents.v3.DocumentService_Service;
import no.gecko.ephorte.services.functions.v2.FunctionsIdentity;
import no.gecko.ephorte.services.functions.v2.FunctionsService;
import no.gecko.ephorte.services.functions.v2.FunctionsService_Service;
import no.gecko.ephorte.services.objectmodel.v3.en.DataObjectT;
import no.gecko.ephorte.services.objectmodel.v3.en.ObjectIdentity;
import no.gecko.ephorte.services.objectmodel.v3.en.ObjectModelService;
import no.gecko.ephorte.services.objectmodel.v3.en.ObjectModelService_Service;
import no.gecko.ncore.client.config.ClientConfig;

import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.developer.WSBindingProvider;

class AbstractClient {

	private no.gecko.ephorte.services.documents.v3.ObjectFactory docFactory = new no.gecko.ephorte.services.documents.v3.ObjectFactory();
	private no.gecko.ephorte.services.functions.v2.ObjectFactory funcFactory = new no.gecko.ephorte.services.functions.v2.ObjectFactory();

	private DocumentIdentity docIdentity;
	private ObjectIdentity objIdentity;
	private FunctionsIdentity funcIdentity;

	private DocumentService docClient;
	private ObjectModelService objClient;
	private FunctionsService funcClient;

	protected class SOAPHeaders {
		public final static String FileName = "FileName";
		public final static String Identifier = "Identifier";
	}

	public AbstractClient() {

		String username = ClientConfig.getUsername();
		String password = ClientConfig.getPassword();
		String database = ClientConfig.getDatabase();

		objIdentity = new ObjectIdentity();
		objIdentity.setUserName(username);
		objIdentity.setPassword(password);
		objIdentity.setDatabase(database);

		docIdentity = new DocumentIdentity();
		docIdentity.setUserName(username);
		docIdentity.setPassword(username);
		docIdentity.setDatabase(database);

		funcIdentity = new FunctionsIdentity();
		funcIdentity.setUserName(username);
		funcIdentity.setPassword(password);
		funcIdentity.setDatabase(database);

		try {
			ObjectModelService_Service objService = new ObjectModelService_Service();

			DocumentService_Service docService = new DocumentService_Service();

			FunctionsService_Service funcService = new FunctionsService_Service();

			switch (ClientConfig.getObjectModelService().getBinding()) {

			case HTTP:
				objClient = objService.getWSHttpBindingObjectModelService();
				break;
			case HTTPS:
				objClient = objService.getWSHttpBindingObjectModelService1();
				break;
			}

			switch (ClientConfig.getDocumentService().getBinding()) {

			case HTTP:
				docClient = docService.getBasicHttpBindingDocumentService();
				break;
			case HTTPS:
				docClient = docService.getBasicHttpBindingDocumentService1();
				break;
			}

			switch (ClientConfig.getFunctionsService().getBinding()) {
			case HTTP:
				funcClient = funcService.getWSHttpBindingFunctionsService();
				break;
			case HTTPS:
				funcClient = funcService.getWSHttpBindingFunctionsService1();
				break;
			}

			((BindingProvider) objClient).getRequestContext().put(
					BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					ClientConfig.getObjectModelService().getAddress());

			((BindingProvider) docClient).getRequestContext().put(
					BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					ClientConfig.getDocumentService().getAddress());

			((BindingProvider) funcClient).getRequestContext().put(
					BindingProvider.ENDPOINT_ADDRESS_PROPERTY,
					ClientConfig.getFunctionsService().getAddress());

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	protected no.gecko.ephorte.services.documents.v3.ObjectFactory getDocFactory() {
		return docFactory;
	}

	protected no.gecko.ephorte.services.functions.v2.ObjectFactory getFuncFactory() {
		return funcFactory;
	}

	protected DocumentIdentity getDocIdentity() {
		return docIdentity;
	}

	protected ObjectIdentity getObjIdentity() {
		return objIdentity;
	}

	protected FunctionsIdentity getFuncIdentity() {
		return funcIdentity;
	}

	protected DocumentService getDocClient() {
		return docClient;
	}

	protected ObjectModelService getObjClient() {
		return objClient;
	}

	protected FunctionsService getFuncClient() {
		return funcClient;
	}

	protected WSBindingProvider getDocProvider() {
		return (WSBindingProvider) docClient;
	}

	protected Map<String, String> getSOAPHeaders(WSBindingProvider provider) {

		Map<String, String> headers = new HashMap<String, String>();

		Iterator<Header> headerIter = provider.getInboundHeaders().iterator();
		while (headerIter.hasNext()) {
			Header header = (Header) headerIter.next();
			headers.put(header.getLocalPart(), header.getStringContent());
		}

		return headers;
	}

	protected void javaToSerRefs(DataObjectT[] dataObjects) throws Exception {

		for (DataObjectT dataObject : dataObjects) {

			Field[] fields = dataObject.getClass().getDeclaredFields();

			for (Field field : fields) {

				field.setAccessible(true);

				Object fieldValue = field.get(dataObject);

				if (fieldValue == null) {
					continue;
				}

				if (DataObjectT.class.isAssignableFrom(fieldValue.getClass())) {

					DataObjectT ref = createRef((DataObjectT) fieldValue);

					field.set(dataObject, ref);
				}

			}
		}
	}

	protected void restoreJavaRefs(DataObjectT... dataObjects) throws Exception {

		for (DataObjectT dataObject : dataObjects) {

			Field[] fields = dataObject.getClass().getDeclaredFields();

			for (Field field : fields) {

				field.setAccessible(true);

				Object fieldValue = field.get(dataObject);

				if (fieldValue == null) {
					continue;
				}

				if (DataObjectT.class.isAssignableFrom(fieldValue.getClass())) {

					// point to the original object
					Object originalObject = ((DataObjectT) fieldValue).getRef();
					field.set(dataObject, originalObject);

					// remove the serialization reference
					((DataObjectT) fieldValue).setRef(null);
				}
			}
		}
	}

	protected void updateOriginalObjects(DataObjectT[] originalObjects,
			List<DataObjectT> newObjects) throws Exception {

		if (originalObjects.length != newObjects.size()) {
			throw new Exception("Object lists have different sizes");
		}

		for (int c = 0; c < newObjects.size(); c++) {

			DataObjectT newObject = newObjects.get(c);
			DataObjectT originalObject = originalObjects[c];

			Field[] newObjectFields = newObject.getClass().getDeclaredFields();

			for (Field field : newObjectFields) {

				field.setAccessible(true);
				Object newObjectFieldValue = field.get(newObject);

				// ignore DataObjectT fields, i.e. preserve original Java
				// references
				if (newObjectFieldValue == null
						|| DataObjectT.class
								.isAssignableFrom(newObjectFieldValue
										.getClass())) {
					continue;
				}

				if (newObjectFieldValue != null) {
					field.set(originalObject, newObjectFieldValue);
				}
			}
		}
	}

	private static DataObjectT createRef(DataObjectT to) throws Exception {

		if (to.getSerId() == null || to.getSerId().isEmpty()) {
			to.setSerId(UUID.randomUUID().toString());
		}

		DataObjectT obj = to.getClass().newInstance();
		obj.setRef(to);

		return obj;
	}

}
