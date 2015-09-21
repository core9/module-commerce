package io.core9.commerce.cart;

import io.core9.commerce.CommerceDataHandlerHelper;
import io.core9.commerce.cart.lineitem.LineItem;
import io.core9.commerce.cart.lineitem.LineItemFactory;
import io.core9.plugin.server.request.Request;
import io.core9.plugin.server.request.RequestUtils;
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
	
	@InjectPlugin
	private LineItemFactory factory;
	
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

	protected Cart handleCartCall(Request request, Cart cart, Map<String, Object> context) {
		if(context == null || (context.get("handled") != null && (Boolean) context.get("handled"))) {
			return cart;
		}
		switch(request.getMethod()) {
		case POST:
			if(context.get("op") == null) {
				return cart;
			} else {
				switch((String) context.get("op")) {
				case "delete":
					deleteItemFromCart(request, cart, context);
					RequestUtils.addMessage(request, "The product has been deleted from your cart.");
					break;
				case "update":
					try {
						updateItemInCart(cart, context);
						RequestUtils.addMessage(request, "The product in your cart has been updated.");
					} catch (CartException e) {
						RequestUtils.addMessage(request, e.getMessage(), e.getArgs());
					}
					break;
				default:
					try {
						addItemToCart(request, cart, context);
						RequestUtils.addMessage(request, "The product has been added to your cart.");
					} catch (CartException e) {
						RequestUtils.addMessage(request, e.getMessage(), e.getArgs());
					}
					break;
				}
			}
			break;
		case DELETE:
			deleteItemFromCart(request, cart, context);
			RequestUtils.addMessage(request, "The product has been deleted from your cart.");
			break;
		case PUT:
			try {
				updateItemInCart(cart, context);
				RequestUtils.addMessage(request, "The product in your cart has been updated.");
			} catch (CartException e) {
				RequestUtils.addMessage(request, e.getMessage(), e.getArgs());
			}
			break;
		default:
			break;
		}
		if(cart.validates(request)) {
			helper.saveCart(request, cart);
		}
		context.put("handled", true);
		return cart;
	}
	
	/**
	 * Remove a LineItem from the cart
	 * @param request
	 * @param cart
	 * @param context
	 */
	public void deleteItemFromCart(Request request, Cart cart, Map<String, Object> context) {
		if(context.get("itemid") != null) {
			LineItem item = cart.getItems().get(context.get("itemid"));
			if(item == null) {
				return;
			} else {
				item.delete();
				cart.getItems().remove(item.getId());
			}
		}
	}
	
	/**
	 * Update the quantity of a cart item
	 * @param cart
	 * @param context
	 * @throws CartException 
	 */
	public void updateItemInCart(Cart cart, Map<String, Object> context) throws CartException {
		LineItem item = cart.getItems().get(context.get("itemid"));
		if(context.get("quantity") != null) {
			try {
				if(Integer.parseInt((String) context.get("quantity")) < 1) {
					throw new CartException("The minimum amount is 1");
				}
			} catch (NumberFormatException e) {
				throw new CartException("The minimum amount is 1");
			}
		}
		item.parse(context);
	}

	/**
	 * Add a new item to the cart
	 * @param cart
	 * @param context
	 * @throws CartException 
	 */
	public void addItemToCart(Request req, Cart cart, Map<String, Object> context) throws CartException {
		int quantity = Integer.parseInt((String) context.get("quantity"));
		if (quantity > 0 && isValid(context)) {
			LineItem item = factory.create(context);
			item.setQuantity(quantity);
			cart.addItem(req, item);
		} else {
			throw new CartException("Your request couldn't be validated");
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
