package io.core9.commerce;

import io.core9.commerce.checkout.CheckoutProcessor;
import io.core9.commerce.checkout.Order;
import io.core9.plugin.server.request.Request;
import io.core9.plugin.widgets.datahandler.DataHandler;
import io.core9.plugin.widgets.datahandler.DataHandlerFactoryConfig;
import io.core9.plugin.widgets.datahandler.factories.BundleDataHandler;

import java.util.HashMap;
import java.util.Map;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

@PluginImplementation
public class CommerceDataHandlerImpl<T extends CommerceDataHandlerConfig> implements CommerceDataHandlerFactory<T> {

	//TODO Remove with PROCESSORS functionality
	private final Map<String, CheckoutProcessor> checkoutProcessors = new HashMap<String, CheckoutProcessor>();
	
	//TODO Remove with PROCESSORS functionality
	@InjectPlugin
	private CommerceDataHandlerHelper helper;
	
	@InjectPlugin
	private BundleDataHandler<T> bundle;
	
	@Override
	public String getName() {
		return "Commerce-Bundle";
	}

	@Override
	public Class<? extends DataHandlerFactoryConfig> getConfigClass() {
		return CommerceDataHandlerConfig.class;
	}

	@Override
	public DataHandler<T> createDataHandler(DataHandlerFactoryConfig options) {
		@SuppressWarnings("unchecked")
		final T config = (T) options;
		final DataHandler<T> bundleHandler = bundle.createDataHandler(config);
		
		return new DataHandler<T>() {
			
			@Override
			public Map<String, Object> handle(Request req) {
				//TODO Remove with PROCESSORS functionality
				Order order = helper.getOrder(req);
				if(order == null) {
					order = helper.createOrder(req);
				}
				for(String processor : config.getProcessors()) {
					checkoutProcessors.get(processor).process(order);
				}
				helper.saveOrder(req, order);
				
				Map<String,Object> result = bundleHandler.handle(req);
				switch (req.getMethod()) {
				case POST:
					req.getResponse().sendRedirect(301, config.getNextStep());
					break;
				default:
					break;
				}
				return result;
			}

			@Override
			public T getOptions() {
				return config;
			}
		};
	}

	@Override
	public void registerProcessor(CheckoutProcessor processor) {
		checkoutProcessors.put(processor.getName(), processor);
	}	
}
