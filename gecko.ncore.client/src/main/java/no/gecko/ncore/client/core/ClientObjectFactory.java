package no.gecko.ncore.client.core;

import org.apache.commons.pool.PoolableObjectFactory;

class ClientObjectFactory implements PoolableObjectFactory<NCoreClient> {

	public void activateObject(NCoreClient client) throws Exception {

	}

	public void destroyObject(NCoreClient client) throws Exception {

	}

	public NCoreClient makeObject() throws Exception {

		return new NCoreClient();
	}

	public void passivateObject(NCoreClient client) throws Exception {

	}

	public boolean validateObject(NCoreClient client) {

		return false;
	}

}
