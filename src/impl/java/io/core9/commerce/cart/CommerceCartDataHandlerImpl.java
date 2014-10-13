package io.core9.commerce.cart;

import io.core9.commerce.CommerceDataHandlerConfig;
import io.core9.commerce.cart.old.Cart;
import io.core9.module.auth.AuthenticationPlugin;
import io.core9.module.auth.Session;
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
public class CommerceCartDataHandlerImpl implements CommerceCartDataHandler {
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	@InjectPlugin
	private AuthenticationPlugin auth;
	
	private String nextStep = "/checkout";

	@Override
	public void setNextStep(String nextStep) {
		this.nextStep = nextStep;
	}

	@Override
	public String getNextStep() {
		return nextStep;
	}

	@Override
	public String getName() {
		return "Commerce-Cart";
	}

	@Override
	public Class<? extends DataHandlerFactoryConfig> getConfigClass() {
		return CommerceDataHandlerConfig.class;
	}

	@Override
	public DataHandler<CommerceDataHandlerConfig> createDataHandler(DataHandlerFactoryConfig options) {
		return new DataHandler<CommerceDataHandlerConfig>() {

			@Override
			public Map<String, Object> handle(Request req) {
				Session session = auth.getUser(req).getSession();
				Cart cart = (Cart) session.getAttribute("cart");
				if(cart == null) {
					cart = new Cart();
				}
				Map<String,Object> result = new HashMap<String,Object>();
				result.put("items", MAPPER.convertValue(cart.getItems().values(), new TypeReference<List<Object>>(){}));
				result.put("total", cart.getTotal());
				return result;
			}

			@Override
			public CommerceDataHandlerConfig getOptions() {
				return (CommerceDataHandlerConfig) options;
			}
		};
	}
	

}
