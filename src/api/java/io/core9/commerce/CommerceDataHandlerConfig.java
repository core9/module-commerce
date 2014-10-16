package io.core9.commerce;

import java.util.List;

import io.core9.plugin.widgets.datahandler.factories.BundleDataHandlerFactoryOptions;

public class CommerceDataHandlerConfig extends BundleDataHandlerFactoryOptions {

	private String nextStep;
	private List<String> processors;

	public String getNextStep() {
		return nextStep;
	}

	public void setNextStep(String nextStep) {
		this.nextStep = nextStep;
	}

	public List<String> getProcessors() {
		return processors;
	}

	public void setProcessors(List<String> processors) {
		this.processors = processors;
	}
}
