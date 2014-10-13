package io.core9.commerce.checkout;

import java.util.Map;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import io.core9.commerce.CommerceStepDataHandlerConfig;
import io.core9.plugin.server.request.Request;
import io.core9.plugin.widgets.datahandler.DataHandler;
import io.core9.plugin.widgets.datahandler.DataHandlerFactoryConfig;

@PluginImplementation
public class BillingDataHandlerImpl implements BillingDataHandler {

	@Override
	public String getName() {
		return "Commerce-Billing";
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
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public CommerceStepDataHandlerConfig getOptions() {
				// TODO Auto-generated method stub
				return null;
			}
		};
	}

}
