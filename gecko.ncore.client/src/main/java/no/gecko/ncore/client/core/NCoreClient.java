package no.gecko.ncore.client.core;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.xml.bind.JAXBElement;

import no.gecko.ephorte.services.documents.v3.CheckinMessageE;
import no.gecko.ephorte.services.documents.v3.CheckoutRequestE;
import no.gecko.ephorte.services.documents.v3.CheckoutResponseMessageE;
import no.gecko.ephorte.services.documents.v3.DocumentCriteriaT;
import no.gecko.ephorte.services.documents.v3.UploadMessageE;
import no.gecko.ephorte.services.functions.v2.ArrayOfFunctionDescriptor;
import no.gecko.ephorte.services.functions.v2.FunctionDescriptor;
import no.gecko.ephorte.services.functions.v2.FunctionResult;
import no.gecko.ephorte.services.objectmodel.v3.en.ArrayOfDataObjectT;
import no.gecko.ephorte.services.objectmodel.v3.en.DataObjectT;
import no.gecko.ephorte.services.objectmodel.v3.en.FilteredQueryArgumentsT;
import no.gecko.ephorte.services.objectmodel.v3.en.QueryResultT;

import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfanyTypeT;
import com.microsoft.schemas._2003._10.serialization.arrays.ArrayOfstringT;

class NCoreClient extends AbstractClient {

	public NCoreClient() {
		// empty constructor
	}

	public List<FunctionDescriptor> queryFunctionDescriptors() {

		ArrayOfFunctionDescriptor descriptors = getFuncClient()
				.queryFunctionDescriptors(getFuncIdentity());

		return descriptors.getFunctionDescriptor();
	}

	public FunctionResult executeFunction(String name, Object... params) {

		ArrayOfanyTypeT arrParams = new ArrayOfanyTypeT();
		for (Object param : params) {
			arrParams.getAnyType().add(param);
		}

		FunctionResult result = getFuncClient().executeFunction(
				getFuncIdentity(), name, arrParams);

		return result;
	}

	public String uploadFile(String fileName, String storageIdentifier,
			byte[] contents) throws Exception {

		UploadMessageE paramContents = new UploadMessageE();
		paramContents.setContent(contents);

		JAXBElement<String> paramFileName = getDocFactory().createFileName(
				fileName);
		JAXBElement<String> paramStorageIdentifier = getDocFactory()
				.createStorageIdentifier(storageIdentifier);

		getDocProvider().setOutboundHeaders(
				getDocFactory().createEphorteIdentity(getDocIdentity()),
				paramFileName, paramStorageIdentifier);

		getDocClient().uploadFile(paramContents);

		return getSOAPHeaders(getDocProvider()).get(SOAPHeaders.FileName);
	}

	public void checkin(Integer documentDescriptionId, String variantFormat,
			Integer version, UUID uuid, String path, byte[] contents) {

		CheckinMessageE checkin = new CheckinMessageE();
		checkin.setContent(contents);

		DocumentCriteriaT tmpDocumentCriteria = getDocFactory()
				.createDocumentCriteriaT();

		tmpDocumentCriteria.setDocumentId(documentDescriptionId);
		tmpDocumentCriteria.setEphorteIdentity(getDocIdentity());
		tmpDocumentCriteria.setVariant(variantFormat);
		tmpDocumentCriteria.setVersion(version);
		JAXBElement<DocumentCriteriaT> docCriteria = getDocFactory()
				.createDocumentCriteria(tmpDocumentCriteria);

		JAXBElement<String> paramGuid = getDocFactory().createGuid(
				uuid.toString());

		JAXBElement<String> paramPath = getDocFactory().createPath(path);

		getDocProvider().setOutboundHeaders(docCriteria, paramGuid, paramPath);

		getDocClient().checkin(checkin);

		Map<String, String> headers = getSOAPHeaders(getDocProvider());

		for (String header : headers.keySet()) {
			System.out.println(header + " : " + headers.get(header));
		}
	}

	public byte[] checkout(Integer documentDescriptionId, Integer version,
			String variantFormat) {

		JAXBElement<Integer> documentId = getDocFactory().createDocumentId(
				documentDescriptionId);

		JAXBElement<String> variant = null;
		if (variantFormat != null) {
			variant = getDocFactory().createVariant(variantFormat);
		}

		JAXBElement<Integer> ver = getDocFactory().createVersion(version);

		getDocProvider().setOutboundHeaders(documentId,
				getDocFactory().createIdentity(getDocIdentity()), variant, ver);

		CheckoutResponseMessageE checkoutResponse = getDocClient().checkout(
				new CheckoutRequestE());

		return checkoutResponse.getContent();
	}

	public List<DataObjectT> filteredQuery(String objectName,
			String filterExpression, String[] relatedObjects,
			Integer skipCount, Integer takeCount) throws Exception {

		if (relatedObjects == null) {
			relatedObjects = new String[] {};
		}

		FilteredQueryArgumentsT queryArgs = new FilteredQueryArgumentsT();
		queryArgs.setDataObjectName(objectName);
		queryArgs.setFilterExpression(filterExpression);

		ArrayOfstringT arrRelated = new ArrayOfstringT();
		for (String relatedObject : relatedObjects) {
			arrRelated.getString().add(relatedObject);
		}

		queryArgs.setRelatedObjects(arrRelated);

		if (skipCount != null && takeCount != null) {
			queryArgs.setSkipCount(skipCount);
			queryArgs.setTakeCount(takeCount);
		}

		QueryResultT result = getObjClient().filteredQuery(getObjIdentity(),
				queryArgs);

		return result.getDataObjects().getDataObject();
	}

	public void insert(DataObjectT... dataObjects) throws Exception {

		// convert Java references to serialization references
		javaToSerRefs(dataObjects);

		ArrayOfDataObjectT arrDataObjects = new ArrayOfDataObjectT();
		for (DataObjectT dataObject : dataObjects) {
			arrDataObjects.getDataObject().add(dataObject);
		}

		ArrayOfDataObjectT result = getObjClient().insert(getObjIdentity(),
				arrDataObjects);

		List<DataObjectT> createdObjects = result.getDataObject();

		// restore original Java references
		restoreJavaRefs(dataObjects);

		// update original objects
		updateOriginalObjects(dataObjects, createdObjects);
	}

	public void update(DataObjectT... dataObjects) {

		ArrayOfDataObjectT arrDataObjects = new ArrayOfDataObjectT();
		for (DataObjectT dataObject : dataObjects) {
			arrDataObjects.getDataObject().add(dataObject);
		}

		getObjClient().update(getObjIdentity(), arrDataObjects);
	}

}
