package io.core9.commerce;

import io.core9.commerce.checkout.CheckoutProcessor;
import io.core9.core.plugin.Core9Plugin;
import io.core9.plugin.widgets.datahandler.DataHandlerFactory;

/**
 * @author mark
 *
 */
public interface CommerceDataHandlerFactory<T extends CommerceDataHandlerConfig> extends DataHandlerFactory<T>, Core9Plugin {
	
	void registerProcessor(CheckoutProcessor processor);
}
