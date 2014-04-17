package io.core9.commerce.cart;

import io.core9.module.auth.AuthenticationPlugin;
import io.core9.module.auth.Session;
import io.core9.plugin.server.request.Request;
import io.core9.plugin.widgets.datahandler.DataHandler;
import io.core9.plugin.widgets.datahandler.DataHandlerDefaultConfig;
import io.core9.plugin.widgets.datahandler.DataHandlerFactoryConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@PluginImplementation
public class CartDataHandlerImpl implements CartDataHandler {
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	@InjectPlugin
	private AuthenticationPlugin auth;


	@Override
	public String getName() {
		return "Cart";
	}

	@Override
	public Class<? extends DataHandlerFactoryConfig> getConfigClass() {
		return DataHandlerDefaultConfig.class;
	}

	@Override
	public DataHandler<DataHandlerDefaultConfig> createDataHandler(final DataHandlerFactoryConfig options) {
		return new DataHandler<DataHandlerDefaultConfig>() {

			@Override
			public Map<String, Object> handle(Request req) {
				Session session = auth.getUser(req).getSession();
				Cart cart = (Cart) session.getAttribute("cart");
				if(cart == null) {
					cart = new CartImpl();
				}
				Map<String,Object> result = new HashMap<String,Object>();
				result.put("items", MAPPER.convertValue(cart.getItems().values(), new TypeReference<List<Object>>(){}));
				int total = 0;
				for(LineItem item : cart.getItems().values()) {
					total += (item.getPrice() * item.getQuantity());
				}
				result.put("total", total);
				return result;
			}

			@Override
			public DataHandlerDefaultConfig getOptions() {
				return (DataHandlerDefaultConfig) options;
			}
		};
	}

}
