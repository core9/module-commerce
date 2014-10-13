package io.core9.commerce.checkout.old;

import io.core9.plugin.widgets.datahandler.DataHandlerFactoryConfig;

import java.util.List;

public class FeedbackDataHandlerConfig extends CheckoutDataHandlerConfig implements DataHandlerFactoryConfig {
	
	private List<String> orderIdentifiers;

	public List<String> getOrderIdentifiers() {
		return orderIdentifiers;
	}

	public void setOrderIdentifiers(List<String> orderIdentifiers) {
		this.orderIdentifiers = orderIdentifiers;
	}

	
}
