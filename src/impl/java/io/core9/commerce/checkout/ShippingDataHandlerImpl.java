package io.core9.commerce.checkout;

import io.core9.commerce.CommerceDataHandlerHelper;
import io.core9.commerce.CommerceStepDataHandlerConfig;
import io.core9.plugin.database.repository.DataUtils;
import io.core9.plugin.server.request.Request;
import io.core9.plugin.widgets.datahandler.ContextualDataHandler;
import io.core9.plugin.widgets.datahandler.DataHandler;
import io.core9.plugin.widgets.datahandler.DataHandlerFactoryConfig;

import java.util.HashMap;
import java.util.Map;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

@PluginImplementation
public class ShippingDataHandlerImpl implements ShippingDataHandler {

	@InjectPlugin
	private CommerceDataHandlerHelper helper;

	@Override
	public String getName() {
		return "Commerce-Shipping";
	}

	@Override
	public Class<? extends DataHandlerFactoryConfig> getConfigClass() {
		return CommerceStepDataHandlerConfig.class;
	}

	@Override
	public DataHandler<CommerceStepDataHandlerConfig> createDataHandler(DataHandlerFactoryConfig options) {
		return new ContextualDataHandler<CommerceStepDataHandlerConfig>() {

			@Override
			public Map<String, Object> handle(Request req, Map<String,Object> context) {
				Order order = helper.getOrder(req);
				Map<String,Object> result = new HashMap<String, Object>();
				if(context != null && (context.get("handled") == null || (Boolean) context.get("handled") == false)) {
					handleShipping(req, order, context);
					context.put("handled", true);
				}
				if(order != null) {
					result.put("shipping", DataUtils.toMap(order.getShipping()));
				}
				return result;
			}

			@Override
			public CommerceStepDataHandlerConfig getOptions() {
				return (CommerceStepDataHandlerConfig) options;
			}
		};
	}

	protected void handleShipping(Request req, Order order, Map<String, Object> context) {
		if(order == null) {
			order = helper.createOrder(req);
		}
		order.setShipping(DataUtils.toObject(context, Address.class));
	}
}
