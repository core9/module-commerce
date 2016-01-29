package io.core9.commerce.payment;

import java.util.HashMap;
import java.util.Map;

import io.core9.commerce.CommerceDataHandlerHelper;
import io.core9.commerce.checkout.Order;
import io.core9.plugin.server.request.Request;
import io.core9.plugin.widgets.datahandler.DataHandler;
import io.core9.plugin.widgets.datahandler.DataHandlerDefaultConfig;
import io.core9.plugin.widgets.datahandler.DataHandlerFactoryConfig;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

@PluginImplementation
public class InvoiceVerifierDataHandlerImpl<T extends DataHandlerDefaultConfig> implements InvoiceVerifierDataHandler<T> {
	
	@InjectPlugin
	private CommerceDataHandlerHelper helper;

	@Override
	public String getName() {
		return "Payment-Invoice-Verifier";
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
				Order order = helper.getRawOrder(req);
				order.setStatus("pending");
				helper.saveOrder(req, order);
				return new HashMap<String,Object>();
			}

			@SuppressWarnings("unchecked")
			@Override
			public T getOptions() {
				return (T) options;
			}
			
		};
	}

}
