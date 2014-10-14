package io.core9.commerce.checkout;

import io.core9.commerce.CommerceStepDataHandlerConfig;
import io.core9.commerce.payment.old.PaymentMethod;
import io.core9.plugin.database.Database;
import io.core9.plugin.server.VirtualHost;
import io.core9.plugin.server.request.Request;
import io.core9.plugin.widgets.datahandler.DataHandler;
import io.core9.plugin.widgets.datahandler.DataHandlerFactoryConfig;

import java.util.HashMap;
import java.util.Map;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

@PluginImplementation
public class PaymentMethodDataHandlerImpl implements PaymentMethodDataHandler {

	@InjectPlugin
	private Database db;

	@Override
	public String getName() {
		return "Commerce-PaymentMethod";
	}

	@Override
	public Class<? extends DataHandlerFactoryConfig> getConfigClass() {
		return CommerceStepDataHandlerConfig.class;
	}

	@Override
	public DataHandler<CommerceStepDataHandlerConfig> createDataHandler(DataHandlerFactoryConfig options) {
		return new DataHandler<CommerceStepDataHandlerConfig>() {

			@Override
			public Map<String, Object> handle(Request req) {
				VirtualHost vhost = req.getVirtualHost();
				Map<String, Object> result = new HashMap<String, Object>();
				result.put(
						"paymentmethods",
						db.getMultipleResults(
								vhost.getContext("database"), 
								vhost.getContext("prefix") + "configuration", 
								PaymentMethod.DEFAULT_QUERY));
				return result;
			}

			@Override
			public CommerceStepDataHandlerConfig getOptions() {
				return (CommerceStepDataHandlerConfig) options;
			}
		};
	}

}
