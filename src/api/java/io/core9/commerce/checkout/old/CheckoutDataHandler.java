package io.core9.commerce.checkout.old;

import io.core9.core.boot.BootStrategy;
import io.core9.core.plugin.Core9Plugin;
import io.core9.plugin.widgets.datahandler.DataHandlerFactory;

public interface CheckoutDataHandler extends DataHandlerFactory<CheckoutDataHandlerConfig>, BootStrategy, Core9Plugin {
	
}
