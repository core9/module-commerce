package io.core9.commerce.checkout;

import io.core9.commerce.CommerceDataHandlerHelper;
import io.core9.plugin.database.repository.DataUtils;
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
public class BillingDataHandlerImpl<T extends DataHandlerDefaultConfig> implements BillingDataHandler<T> {
	
	@InjectPlugin
	private CommerceDataHandlerHelper helper;

	@Override
	public String getName() {
		return "Commerce-Billing";
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
				Order order = helper.getRawOrder(req);
				Map<String,Object> result = new HashMap<String, Object>();
				order = handleBilling(req, order, context);
				if(order != null) {
					if(order.getBilling() != null) {
						result.put("billing", DataUtils.toMap(order.getBilling()));
					} else {
						result.put("billing", context);
					}
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

	protected Order handleBilling(Request req, Order order, Map<String, Object> context) {
		if(context == null) {
			return order;
		}
		if(context.get("handled") != null && (Boolean) context.get("handled")) {
			return order;
		}
		Address billing = DataUtils.toObject(context, Address.class);
		if(billing.validates(req)) {
			order.setBilling(DataUtils.toObject(context, Address.class));
		}
		helper.saveOrder(req, order);
		context.put("handled", true);
		return order;
	}

}
