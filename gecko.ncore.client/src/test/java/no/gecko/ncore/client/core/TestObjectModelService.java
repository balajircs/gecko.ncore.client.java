package no.gecko.ncore.client.core;

import java.util.List;
import java.util.UUID;

import junit.framework.Assert;
import no.gecko.ephorte.services.objectmodel.v3.en.DataObjectT;
import no.gecko.ephorte.services.objectmodel.v3.en.dataobjects.CaseT;
import no.gecko.ephorte.services.objectmodel.v3.en.dataobjects.RegistryEntryT;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestObjectModelService {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {

		TestInitializer.init();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInsert() throws Exception {

		// create a case
		CaseT ca = new CaseT();
		ca.setTitle("case : " + UUID.randomUUID().toString());

		// create a registry entry
		RegistryEntryT re = new RegistryEntryT();
		re.setTitle("registry entry : " + UUID.randomUUID().toString());
		re.setRegistryEntryTypeId("X");
		re.setCase(ca);

		// insert the case and registry entry
		NCore.Objects.insert(ca, re);

		// check if original referencing is preserved
		Assert.assertTrue(ca.equals(re.getCase()));

		// check if original objects have been correctly updated
		Assert.assertTrue(ca.getId() != null);
		Assert.assertTrue(re.getId() != null);
	}

	@Test
	public void testUpdate() throws Exception {

		// create a case
		CaseT ca = new CaseT();
		ca.setTitle("case : " + UUID.randomUUID().toString());

		// insert the case
		NCore.Objects.insert(ca);

		// create a new case title
		String newTitle = ca.getTitle() + " - updated";
		ca.setTitle(newTitle);

		// update the case title
		NCore.Objects.update(ca);

		// get the updated case
		List<DataObjectT> result = NCore.Objects.filteredQuery(
				ObjectModel.Case, "Id=" + ca.getId(), new String[] {}, null,
				null);
		String updatedTitle = ((CaseT) result.get(0)).getTitle();

		// check if the title has been properly updated
		Assert.assertTrue(newTitle.equals(updatedTitle));
	}

	@Test
	public void testFilteredQuery1() throws Exception {

		// search for a non existing case
		List<DataObjectT> result = NCore.Objects.filteredQuery(
				ObjectModel.Case, "Id<0", null, null, null);

		// check if something is returned
		Assert.assertTrue(result.size() == 0);
	}

	@Test
	public void testFilteredQuery2() throws Exception {

		// perform a paged query
		List<DataObjectT> result = NCore.Objects.filteredQuery(
				ObjectModel.Case, "Id>0", null, 0, 1);

		// check if only a single item is returned
		Assert.assertTrue(result.size() == 1);
	}

}
