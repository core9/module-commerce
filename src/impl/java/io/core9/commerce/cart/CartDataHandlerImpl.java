package io.core9.commerce.cart;

import io.core9.commerce.CommerceDataHandlerHelper;
import io.core9.commerce.CommerceStepDataHandlerConfig;
import io.core9.commerce.cart.old.CommerceEncryptionPlugin;
import io.core9.plugin.server.request.Request;
import io.core9.plugin.widgets.datahandler.ContextualDataHandler;
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
public class CartDataHandlerImpl implements CartDataHandler {
	
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
		return CommerceStepDataHandlerConfig.class;
	}

	@Override
	public DataHandler<CommerceStepDataHandlerConfig> createDataHandler(DataHandlerFactoryConfig options) {
		return new ContextualDataHandler<CommerceStepDataHandlerConfig>() {
			
			@Override
			public CommerceStepDataHandlerConfig getOptions() {
				return (CommerceStepDataHandlerConfig) options;
			}

			@Override
			public Map<String, Object> handle(Request req, Map<String, Object> context) {
				Cart cart = helper.getCart(req);
				handleCartCall(req, cart, context);
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
		// TODO: Handle redirect
//		if(requestBody.get("redirect") != null) {
//			request.getResponse().sendRedirect(301, (String) requestBody.get("redirect"));
//		} else {
//			request.getResponse().sendJsonArray(new ArrayList<LineItem>(cart.getItems().values()));
//		}
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
