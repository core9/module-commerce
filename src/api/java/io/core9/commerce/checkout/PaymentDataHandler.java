package io.core9.commerce.checkout;

import io.core9.core.plugin.Core9Plugin;
import io.core9.plugin.server.VirtualHost;
import io.core9.plugin.widgets.datahandler.DataHandler;
import io.core9.plugin.widgets.datahandler.DataHandlerDefaultConfig;
import io.core9.plugin.widgets.datahandler.DataHandlerFactory;

public interface PaymentDataHandler<T extends DataHandlerDefaultConfig> extends DataHandlerFactory<T>, Core9Plugin {
	
	/**
	 * Returns the PaymentMethod object of an order
	 * @param vhost
	 * @param order
	 * @return
	 */
	PaymentMethod getPaymentMethod(VirtualHost vhost, Order order);
	
	/**
	 * Returns the datahandler for the paymentmethod
	 * @param vhost
	 * @param method
	 * @return
	 */
	DataHandler<?> getPaymentDataHandler(VirtualHost vhost, PaymentMethod method);
	
	/**
	 * Returns the datahandler for the paymentmethod verifier
	 * @param vhost
	 * @param method
	 * @return
	 */
	DataHandler<?> getPaymentVerifierDataHandler(VirtualHost vhost, PaymentMethod method);
}
