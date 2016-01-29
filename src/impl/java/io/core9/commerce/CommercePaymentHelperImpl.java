package io.core9.commerce;

import java.util.HashMap;
import java.util.Map;

import io.core9.commerce.checkout.Order;
import io.core9.commerce.checkout.PaymentDataHandler;
import io.core9.commerce.checkout.PaymentMethod;
import io.core9.plugin.server.request.Request;
import io.core9.plugin.widgets.datahandler.DataHandler;
import io.core9.plugin.widgets.datahandler.DataHandlerDefaultConfig;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

@PluginImplementation
public class CommercePaymentHelperImpl implements CommercePaymentHelper {
	
	@InjectPlugin
	private PaymentDataHandler<DataHandlerDefaultConfig> paymentHandler;

	@Override
	public Map<String, Object> verifyPayment(Request req, Order order) {
		PaymentMethod method = paymentHandler.getPaymentMethod(req.getVirtualHost(), order);
		DataHandler<?> handler = paymentHandler.getPaymentVerifierDataHandler(req.getVirtualHost(), method);
		return handler == null ? new HashMap<String, Object>() : handler.handle(req);
	}
	
}
