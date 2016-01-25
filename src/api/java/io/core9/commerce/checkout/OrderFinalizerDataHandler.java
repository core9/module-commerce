package io.core9.commerce.checkout;

import io.core9.core.plugin.Core9Plugin;
import io.core9.plugin.server.VirtualHost;
import io.core9.plugin.server.request.Request;
import io.core9.plugin.widgets.datahandler.DataHandlerFactory;

public interface OrderFinalizerDataHandler<T extends OrderFinalizerDataHandlerConfig> extends DataHandlerFactory<T>, Core9Plugin {
	
	/**
	 * Finalize the order
	 * @param req
	 * @param vhost
	 * @param order
	 * @param config
	 */
	void finalizeOrder(Request req, VirtualHost vhost, Order order, T config);

	/**
	 * Check if order can be finalized
	 * @param req
	 * @param vhost
	 * @param order
	 * @return
	 */
	boolean canBeFinalized(Order order);

	/**
	 * Sends an email with a template, filled with the order params
	 * @param config
	 * @param vhost
	 * @param order
	 * @param templateName
	 */
	void sendOrderMail(T config, VirtualHost vhost, Order order, String templateName);

}
