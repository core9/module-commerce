package io.core9.commerce.checkout;

import io.core9.core.plugin.Core9Plugin;
import io.core9.plugin.server.VirtualHost;
import io.core9.plugin.widgets.datahandler.DataHandlerFactory;

public interface OrderFinalizerDataHandler<T extends OrderFinalizerDataHandlerConfig> extends DataHandlerFactory<T>, Core9Plugin {

	void mailOrderConfirmation(T config, VirtualHost vhost, Order order);

}
