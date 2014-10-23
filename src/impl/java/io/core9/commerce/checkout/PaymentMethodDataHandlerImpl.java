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
import java.util.List;
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
				List<Map<String,Object>> methods = db.getMultipleResults(
						vhost.getContext("database"), 
						vhost.getContext("prefix") + "configuration", 
						PaymentMethod.DEFAULT_QUERY); 
				result.put("paymentmethods", methods);
				order = handlePaymentSelection(req, order, context, methods);
				if(order != null) {
					result.put("paymentmethod", order.getPaymentmethod());
				}
				return result;
			}

			@SuppressWarnings("unchecked")
			@Override
			public T getOptions() {
				return (T) options;
			}
		};
	}

	protected Order handlePaymentSelection(Request req, Order order, Map<String, Object> context, List<Map<String, Object>> methods) {
		if(context == null) {
			if(order.getPaymentmethod() == null) {
				req.getResponse().addGlobal("message", "You haven't selected any payment method details");
			}
			return order;
		}
		if(context.get("handled") != null && (Boolean) context.get("handled")) {
			return order;
		}
		String paymentmethod = (String) context.get("paymentmethod");
		if(paymentmethod == null) {
			req.getResponse().addGlobal("message", "Please select a payment method");
		} else {
			boolean found = false;
			for(Map<String,Object> method : methods) {
				if(method.get("name").equals(paymentmethod)) {
					found = true;
					continue;
				}
			}
			if(found) {
				order.setPaymentmethod(paymentmethod);
			} else {
				req.getResponse().addGlobal("message", "You selected an unknown payment method, please select an existing method");
			}
		}
		context.put("handled", true);
		helper.saveOrder(req, order);
		return order;
	}

}
