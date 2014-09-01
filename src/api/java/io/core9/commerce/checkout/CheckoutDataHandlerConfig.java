package io.core9.commerce.checkout;

import io.core9.plugin.widgets.datahandler.DataHandlerDefaultConfig;
import io.core9.plugin.widgets.datahandler.DataHandlerFactoryConfig;

import java.util.List;

public class CheckoutDataHandlerConfig extends DataHandlerDefaultConfig	implements DataHandlerFactoryConfig {
	
	private List<String> processors;
	private String nextStep;
	private boolean destroySession;

	public String getNextStep() {
		return nextStep;
	}

	public void setNextStep(String nextStep) {
		this.nextStep = nextStep;
	}

	public boolean getDestroySession() {
		return destroySession;
	}

	public void setDestroySession(boolean destroySession) {
		this.destroySession = destroySession;
	}

	public List<String> getProcessors() {
		return processors;
	}

	public void setProcessors(List<String> processors) {
		this.processors = processors;
	}

}
