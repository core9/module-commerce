package io.core9.commerce.checkout;

import io.core9.commerce.CommerceDataHandlerHelper;
import io.core9.plugin.database.Database;
import io.core9.plugin.server.VirtualHost;
import io.core9.plugin.server.request.Request;
import io.core9.plugin.widgets.datahandler.ContextualDataHandler;
import io.core9.plugin.widgets.datahandler.DataHandler;
import io.core9.plugin.widgets.datahandler.DataHandlerDefaultConfig;
import io.core9.plugin.widgets.datahandler.DataHandlerFactoryConfig;

import java.util.HashMap;
import java.util.Map;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

@PluginImplementation
public class PaymentMethodDataHandlerImpl<T extends DataHandlerDefaultConfig> implements PaymentMethodDataHandler<T> {
	
	@InjectPlugin
	private CommerceDataHandlerHelper helper;

	@InjectPlugin
	private Database db;

	@Override
	public String getName() {
		return "Commerce-PaymentMethod";
	}

	@Override
	public Class<? extends DataHandlerFactoryConfig> getConfigClass() {
		return DataHandlerDefaultConfig.class;
	}

	@Override
	public DataHandler<T> createDataHandler(DataHandlerFactoryConfig options) {
		return new ContextualDataHandler<T>() {

			@Override
			public Map<String, Object> handle(Request req, Map<String,Object> context) {
				VirtualHost vhost = req.getVirtualHost();
				Map<String, Object> result = new HashMap<String, Object>();
				Order order = helper.getOrder(req);
				if(context != null && (context.get("handled") == null || (Boolean) context.get("handled") == false)) {
					order = handlePaymentSelection(req, order, context);
					context.put("handled", true);
				}
				if(order != null) {
					result.put("paymentmethod", order.getPaymentmethod());
				}
				result.put(
						"paymentmethods",
						db.getMultipleResults(
								vhost.getContext("database"), 
								vhost.getContext("prefix") + "configuration", 
								PaymentMethod.DEFAULT_QUERY));
				return result;
			}

			@SuppressWarnings("unchecked")
			@Override
			public T getOptions() {
				return (T) options;
			}
		};
	}

	protected Order handlePaymentSelection(Request req, Order order, Map<String, Object> context) {
		if(order == null) {
			order = helper.createOrder(req);
		}
		order.setPaymentmethod((String) context.get("paymentmethod"));
		helper.saveOrder(req, order);
		return order;
	}

}
