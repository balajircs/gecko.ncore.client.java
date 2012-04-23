package no.gecko.ncore.client.config;

import no.gecko.ncore.client.core.BindingType;

public class ServiceConfig {

	private String address;
	private BindingType binding;

	public ServiceConfig(String address, String bindingStr) {

		this.address = address;

		if (bindingStr.equalsIgnoreCase("http")) {
			binding = BindingType.HTTP;
		} else if (bindingStr.equalsIgnoreCase("https")) {
			binding = BindingType.HTTPS;
		} else {
			throw new RuntimeException(bindingStr
					+ " is an incorrect binding type");
		}
	}

	public String getAddress() {
		return address;
	}

	public BindingType getBinding() {
		return binding;
	}

}
