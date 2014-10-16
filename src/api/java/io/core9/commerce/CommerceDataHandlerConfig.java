package io.core9.commerce;

import io.core9.plugin.widgets.datahandler.factories.BundleDataHandlerFactoryOptions;

public class CommerceDataHandlerConfig extends BundleDataHandlerFactoryOptions {

	private String nextStep;

	public String getNextStep() {
		return nextStep;
	}

	public void setNextStep(String nextStep) {
		this.nextStep = nextStep;
	}
}
