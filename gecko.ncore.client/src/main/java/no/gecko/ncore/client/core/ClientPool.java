package no.gecko.ncore.client.core;

import no.gecko.ncore.client.config.ClientConfig;

import org.apache.commons.pool.impl.GenericObjectPool;

class ClientPool {

	private static ClientPool instance;
	private static GenericObjectPool<NCoreClient> pool;

	private ClientPool() {

		pool = new GenericObjectPool<NCoreClient>(new ClientObjectFactory());
		pool.setMaxActive(ClientConfig.getClientPoolSize());
		pool.setWhenExhaustedAction(GenericObjectPool.WHEN_EXHAUSTED_BLOCK);

		try {
			for (int c = 0; c < pool.getMaxActive(); c++) {
				pool.addObject();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void init() {
		if (instance == null) {
			instance = new ClientPool();
		}
	}

	public static GenericObjectPool<NCoreClient> get() {
		return pool;
	}

}
