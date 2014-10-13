package io.core9.commerce.cart;

import io.core9.commerce.CommerceDataHandlerConfig;
import io.core9.commerce.CommerceDataHandlerHelper;
import io.core9.commerce.CommerceStepDataHandlerConfig;
import io.core9.commerce.cart.old.Cart;
import io.core9.plugin.server.request.Request;
import io.core9.plugin.widgets.datahandler.DataHandler;
import io.core9.plugin.widgets.datahandler.DataHandlerFactoryConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@PluginImplementation
public class CouponDataHandlerImpl implements CouponDataHandler {
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	@InjectPlugin
	private CommerceDataHandlerHelper helper;
	
	@Override
	public String getName() {
		return "Commerce-Cart";
	}

	@Override
	public Class<? extends DataHandlerFactoryConfig> getConfigClass() {
		return CommerceDataHandlerConfig.class;
	}

	@Override
	public DataHandler<CommerceStepDataHandlerConfig> createDataHandler(DataHandlerFactoryConfig options) {
		return new DataHandler<CommerceStepDataHandlerConfig>() {
			
			@Override
			public Map<String, Object> handle(Request req) {
				Cart cart = helper.getCart(req);
				return getCartAsMap(cart);
			}

			@Override
			public CommerceStepDataHandlerConfig getOptions() {
				return (CommerceStepDataHandlerConfig) options;
			}
		};
	}

	protected Map<String, Object> getCartAsMap(final Cart cart) {
		Map<String,Object> result = new HashMap<String, Object>();
		result.put("items", MAPPER.convertValue(cart.getItems().values(), new TypeReference<List<Object>>(){}));
		result.put("total", cart.getTotal());
		return result;
	}
	

}
