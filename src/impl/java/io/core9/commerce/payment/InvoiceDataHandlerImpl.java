package io.core9.commerce.payment;

import java.util.HashMap;
import java.util.Map;

import io.core9.commerce.CommerceDataHandlerHelper;
import io.core9.commerce.checkout.Order;
import io.core9.plugin.database.repository.DataUtils;
import io.core9.plugin.server.request.Request;
import io.core9.plugin.widgets.datahandler.DataHandler;
import io.core9.plugin.widgets.datahandler.DataHandlerDefaultConfig;
import io.core9.plugin.widgets.datahandler.DataHandlerFactoryConfig;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

@PluginImplementation
public class InvoiceDataHandlerImpl<T extends DataHandlerDefaultConfig> implements InvoiceDataHandler<T> {
	
	@InjectPlugin
	private CommerceDataHandlerHelper helper;

	@Override
	public String getName() {
		return "Payment-Invoice";
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
				Order order = helper.getRawOrder(req);
				if(order != null) {
					result.put("paymentData", DataUtils.toMap(order));
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
