package io.core9.commerce.checkout;

import java.util.HashMap;
import java.util.Map;

import io.core9.commerce.CommerceDataHandlerHelper;
import io.core9.plugin.server.request.Request;
import io.core9.plugin.widgets.datahandler.DataHandler;
import io.core9.plugin.widgets.datahandler.DataHandlerDefaultConfig;
import io.core9.plugin.widgets.datahandler.DataHandlerFactoryConfig;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

@PluginImplementation
public class StatusDataHandlerImpl<T extends DataHandlerDefaultConfig> implements StatusDataHandler<T> {
	
	@InjectPlugin
	private CommerceDataHandlerHelper helper;

	@Override
	public String getName() {
		return "Commerce-Status";
	}

	@Override
	public Class<? extends DataHandlerFactoryConfig> getConfigClass() {
		return DataHandlerDefaultConfig.class;
	}

	@Override
	public DataHandler<T> createDataHandler(DataHandlerFactoryConfig options) {
		return new DataHandler<T>() {

			@Override
			public Map<String, Object> handle(Request req) {
				Map<String,Object> result = new HashMap<String, Object>(2);
				Order order = helper.getOrder(req);
				if(order != null) {
					result.put("status", order.getStatus());
					result.put("message", order.getMessage());
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

}
