package no.gecko.ncore.client.sample;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import no.gecko.ephorte.services.functions.v2.FunctionDescriptor;
import no.gecko.ephorte.services.functions.v2.FunctionResult;
import no.gecko.ephorte.services.objectmodel.v3.en.DataObjectT;
import no.gecko.ephorte.services.objectmodel.v3.en.dataobjects.CasePartyT;
import no.gecko.ephorte.services.objectmodel.v3.en.dataobjects.CaseT;
import no.gecko.ephorte.services.objectmodel.v3.en.dataobjects.DocumentDescriptionT;
import no.gecko.ephorte.services.objectmodel.v3.en.dataobjects.DocumentObjectT;
import no.gecko.ephorte.services.objectmodel.v3.en.dataobjects.RegistryEntryDocumentT;
import no.gecko.ephorte.services.objectmodel.v3.en.dataobjects.RegistryEntryT;
import no.gecko.ncore.client.core.NCore;
import no.gecko.ncore.client.core.ObjectModel;

import org.apache.commons.io.FileUtils;

public class GeckoNCoreClientSample {

	// test files
	static File testFile1 = new File("resources/docs/test.pdf");
	static File testFile2 = new File("resources/docs/test.pdf");

	private static int caseId;
	private static int casePartyId;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

		String configData = FileUtils.readFileToString(new File(
				"resources/config/config.xml"));
		NCore.init(configData);

		createStructure();
		walkStructure();
		doSearches();
		testFunctions();

	}

	private static void createStructure() throws Exception {

		// upload a file
		String uploadedFileName = NCore.Documents.uploadFile("testFile.pdf",
				"ObjectModelService", FileUtils.readFileToByteArray(testFile1));

		// create a case
		CaseT ca = new CaseT();
		ca.setTitle("case : " + getCurrentTime());

		// create a case party
		CasePartyT cp = new CasePartyT();
		cp.setName("Case party 1");
		cp.setCase(ca);

		// create a registry entry
		RegistryEntryT re = new RegistryEntryT();
		re.setTitle("registry entry : " + getCurrentTime());
		re.setRegistryEntryTypeId("X");
		re.setCase(ca);

		// create a document description
		DocumentDescriptionT dd = new DocumentDescriptionT();
		dd.setDocumentTitle("document description : " + getCurrentTime());

		// create a document object
		DocumentObjectT dobj = new DocumentObjectT();
		dobj.setVersionNumber(1);
		dobj.setVariantFormatId("P");
		dobj.setFilePath(uploadedFileName);
		dobj.setFileformatId("PDF");
		dobj.setDocumentDescription(dd);

		// create a registry entry document
		RegistryEntryDocumentT red = new RegistryEntryDocumentT();
		red.setDocumentLinkTypeId("H");
		red.setRegistryEntry(re);
		red.setDocumentDescription(dd);

		// insert the data objects
		NCore.Objects.insert(ca, cp, re, dd, dobj, red);

		System.out.println("Created case : " + ca.getTitle());
		System.out.println("Created case party : " + cp.getName());
		System.out.println("Created registry entry : " + re.getTitle());
		System.out.println("Created document description : "
				+ dd.getDocumentTitle());
		System.out.println("Created document object : " + dobj.getFilePath());

		// create a new document object
		DocumentObjectT dobj2 = new DocumentObjectT();
		dobj2.setVersionNumber(dobj.getVersionNumber() + 1);
		dobj2.setVariantFormatId("P");
		dobj2.setFileformatId("PDF");
		dobj2.setDocumentDescriptionId(dd.getId());

		// insert the new document object
		NCore.Objects.insert(dobj2);

		// check in a new document version
		byte[] testFile2Contents = FileUtils.readFileToByteArray(testFile2);
		NCore.Documents.checkin(dd.getId(), "P", dobj2.getVersionNumber(),
				UUID.randomUUID(), "", testFile2Contents);

		caseId = ca.getId();
		casePartyId = cp.getId();
		System.out.println("case party id  : " + casePartyId);
		System.out.println("case id : " + caseId);
	}

	private static void walkStructure() throws Exception {

		// update the city of the newly created case party
		List<DataObjectT> caseParties = NCore.Objects
				.filteredQuery(ObjectModel.CaseParty, "Id=" + casePartyId
						+ " AND CaseId=" + caseId,
						new String[] { ObjectModel.Case }, null, null);

		CasePartyT cp = (CasePartyT) caseParties.get(0);
		cp.setCity("Oslo");

		NCore.Objects.update(cp);

		// update the title of the case
		CaseT ca = cp.getCase();
		ca.setTitle(ca.getTitle() + " - updated");

		NCore.Objects.update(ca);

		List<DataObjectT> cases = NCore.Objects.filteredQuery(ObjectModel.Case,
				"Id=" + caseId, new String[] {}, null, null);
		ca = (CaseT) cases.get(0);
		System.out.println(ca.getTitle());

		// get the only registry entry in the case we just created
		List<DataObjectT> res = NCore.Objects.filteredQuery(
				ObjectModel.RegistryEntry, "CaseId = " + caseId,
				new String[] {}, null, null);

		RegistryEntryT re = (RegistryEntryT) res.get(0);

		// get the only the first registry entry document and its document
		// description
		List<DataObjectT> reds = NCore.Objects.filteredQuery(
				ObjectModel.RegistryEntryDocument,
				"RegistryEntryId = " + re.getId(),
				new String[] { ObjectModel.DocumentDescription }, null, null);

		RegistryEntryDocumentT red = (RegistryEntryDocumentT) reds.get(0);

		// get all document objects associated with the document description we
		// just fetched
		List<DataObjectT> dobjs = NCore.Objects.filteredQuery(
				ObjectModel.DocumentObject, "DocumentDescriptionId = "
						+ red.getDocumentDescription().getId(),
				new String[] {}, null, null);

		// checkout all document versions
		for (DataObjectT tmpDobj : dobjs) {

			DocumentObjectT dobj = (DocumentObjectT) tmpDobj;

			int version = dobj.getVersionNumber();
			System.out.println(version);

			byte[] document = NCore.Documents.checkout(red
					.getDocumentDescription().getId(), version, "P");

			System.out.println("version = " + version + " , " + "size = "
					+ document.length);
		}

	}

	private static void doSearches() throws Exception {

		System.out.println("~~~~~~ Getting all cases in the system ~~~~~~");

		int index = 0;
		int takeCount = 7;

		// search for all cases
		while (true) {

			List<DataObjectT> cases = NCore.Objects
					.filteredQuery(ObjectModel.Case, "Id>0", new String[] {},
							index, takeCount);

			// no more cases
			if (cases.size() == 0) {
				break;
			}

			System.out.println("~~~~~~~~~~");
			for (DataObjectT dataObject : cases) {

				CaseT ca = (CaseT) dataObject;
				System.out.println(ca.getId() + " : " + ca.getTitle());
			}

			index += takeCount;
		}
	}

	private static void testFunctions() throws Exception {

		List<FunctionDescriptor> funcDescriptors = NCore.Functions
				.queryFunctionDescriptors();

		for (FunctionDescriptor descriptor : funcDescriptors) {
			System.out.println(descriptor.getName());
		}
		System.out.println("~~~~~~~~~~~");

		FunctionResult result = NCore.Functions
				.executeFunction("GetEphorteDBName");

		System.out.println(result.getResultValue());
	}

	private static String getCurrentTime() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		return dateFormat.format(date);
	}

}
