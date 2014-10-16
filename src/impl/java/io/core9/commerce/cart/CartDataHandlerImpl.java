package io.core9.commerce.cart;

import io.core9.commerce.CommerceDataHandlerHelper;
import io.core9.plugin.server.request.Request;
import io.core9.plugin.widgets.datahandler.ContextualDataHandler;
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
public class CartDataHandlerImpl<T extends DataHandlerDefaultConfig> implements CartDataHandler<T> {
	
	private static final ObjectMapper MAPPER = new ObjectMapper();
	
	@InjectPlugin
	private CommerceDataHandlerHelper helper;
	
	@InjectPlugin
	private CommerceEncryptionPlugin encryption;
	
	@Override
	public String getName() {
		return "Commerce-Cart";
	}

	@Override
	public Class<? extends DataHandlerFactoryConfig> getConfigClass() {
		return DataHandlerDefaultConfig.class;
	}

	@Override
	public DataHandler<T> createDataHandler(DataHandlerFactoryConfig options) {
		return new ContextualDataHandler<T>() {
			
			@SuppressWarnings("unchecked")
			@Override
			public T getOptions() {
				return (T) options;
			}

			@Override
			public Map<String, Object> handle(Request req, Map<String, Object> context) {
				Cart cart = helper.getCart(req);
				if(context != null && (context.get("handled") == null || (Boolean) context.get("handled") == false)) {
					handleCartCall(req, cart, context);
					context.put("handled", true);
				}
				return getCartAsMap(cart);
			}
		};
	}
	
	protected Map<String, Object> getCartAsMap(final Cart cart) {
		Map<String,Object> result = new HashMap<String, Object>();
		result.put("items", MAPPER.convertValue(cart.getItems().values(), new TypeReference<List<Object>>(){}));
		result.put("total", cart.getTotal());
		return result;
	}

	protected void handleCartCall(Request request, Cart cart, Map<String, Object> context) {
		switch(request.getMethod()) {
		case POST:
			if(context.get("op") == null) {
				return;
			} else {
				switch((String) context.get("op")) {
				case "delete":
					deleteItemFromCart(cart, context);
					break;
				case "update":
					updateItemInCart(cart, context);
					break;
				default:
					addItemToCart(cart, context);
					break;
				}
			}
			break;
		case DELETE:
			deleteItemFromCart(cart, context);
			break;
		case PUT:
			updateItemInCart(cart, context);
			break;
		default:
			return;
		}
		helper.saveCart(request, cart);
	}
	
	public void deleteItemFromCart(Cart cart, Map<String, Object> context) {
		if(context.get("itemid") != null) {
			cart.getItems().remove(context.get("itemid"));
		}
	}
	
	public void updateItemInCart(Cart cart, Map<String, Object> context) {
		LineItem item = cart.getItems().get(context.get("itemid"));
		if(context.containsKey("quantity")) {
			int quantity = Integer.parseInt((String) context.get("quantity"));
			if(quantity > 0) {
				item.setQuantity(quantity);
			}
		}
	}

	public void addItemToCart(Cart cart, Map<String, Object> context) {
		int quantity = Integer.parseInt((String) context.get("quantity"));
		if(quantity > 0 && isValid(context)) {
			cart.addItem(
				(String) context.get("itemid"),
				quantity, 
				Integer.parseInt((String) context.get("price")), 
				(String) context.get("description"),
				(String) context.get("image"),
				(String) context.get("link"));
		}
	}
	
	private boolean isValid(Map<String,Object> body) {
		String[] fields = getHashFields(body);
		Map<String,Object> validation = new HashMap<String,Object>();
		for(String field : fields) {
			validation.put(field, body.get(field));
		}
		return body.get("hash").equals(encryption.encrypt(validation));
	}
	
	private String[] getHashFields(Map<String,Object> body) {
		String fields = (String) body.get("hashfields");
		fields = fields.replaceAll("\\[", "").replaceAll("\\]", "").replaceAll(" ", "");
		return fields.split(",");
	}

}
