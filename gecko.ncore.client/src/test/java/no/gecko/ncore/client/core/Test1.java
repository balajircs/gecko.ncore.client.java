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

public class Test1 {

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
	public void test1() throws Exception {

		CaseT ca = new CaseT();
		ca.setTitle(UUID.randomUUID().toString());
		
		RegistryEntryT re = new RegistryEntryT();
		re.setTitle(UUID.randomUUID().toString());
		re.setRegistryEntryTypeId("X");
		re.setCase(ca);
		
		NCore.Objects.insert(ca, re);
		
		Assert.assertTrue(ca.getId() != null);
		Assert.assertTrue(re.getId() != null);
		
	}

	@Test
	public void test2() throws Exception {

		List<DataObjectT> dataObjects = NCore.Objects.filteredQuery(
				ObjectModel.Case, "Id=1", new String[] {}, null, null);

		Assert.assertTrue(dataObjects.size() > 0);
		
	}
}
