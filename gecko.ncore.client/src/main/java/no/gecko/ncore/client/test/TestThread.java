package no.gecko.ncore.client.test;

import java.util.List;

import no.gecko.ephorte.services.objectmodel.v3.en.DataObjectT;
import no.gecko.ephorte.services.objectmodel.v3.en.dataobjects.CaseT;
import no.gecko.ncore.client.core.NCore;
import no.gecko.ncore.client.core.ObjectModel;

public class TestThread extends Thread {

	public TestThread(String name) {
		this.setName(name);
	}

	@Override
	public void run() {

		while (true) {

			try {

				List<DataObjectT> dataObjects = NCore.Objects
						.filteredQuery(ObjectModel.Case, "Id = 1",
								new String[] {}, null, null);

				if (dataObjects != null) {
					CaseT ca = (CaseT) dataObjects.get(0);
					System.out.println(getName() + " --> " + ca.getTitle());
				} else {
					System.out.println("null");
				}

			} catch (Exception ex) {
				ex.printStackTrace();
			}

		}

	}

}
