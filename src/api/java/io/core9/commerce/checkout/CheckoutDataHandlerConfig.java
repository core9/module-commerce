package io.core9.commerce.checkout;

import io.core9.plugin.widgets.datahandler.DataHandlerDefaultConfig;
import io.core9.plugin.widgets.datahandler.DataHandlerFactoryConfig;

public class CheckoutDataHandlerConfig extends DataHandlerDefaultConfig	implements DataHandlerFactoryConfig {
	
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

}
