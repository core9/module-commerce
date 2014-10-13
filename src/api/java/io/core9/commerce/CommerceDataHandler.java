package io.core9.commerce;

import io.core9.plugin.widgets.datahandler.DataHandlerFactory;

/**
 * TODO This interface could be transformed to a more general FormDataHandler
 * @author mark
 *
 */
public interface CommerceDataHandler<T extends CommerceDataHandlerConfig> extends DataHandlerFactory<T> {
	
	void setNextStep(String nextStep);
	
	String getNextStep();
	
}
