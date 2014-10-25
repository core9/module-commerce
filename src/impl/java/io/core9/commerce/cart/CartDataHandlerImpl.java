package io.core9.commerce.cart;

import io.core9.commerce.CommerceDataHandlerHelper;
import io.core9.commerce.cart.lineitem.CouponLineItem;
import io.core9.commerce.cart.lineitem.LineItem;
import io.core9.commerce.cart.lineitem.LinkedLineItem;
import io.core9.commerce.coupon.Coupon;
import io.core9.plugin.database.repository.CrudRepository;
import io.core9.plugin.database.repository.NoCollectionNamePresentException;
import io.core9.plugin.database.repository.RepositoryFactory;
import io.core9.plugin.server.request.Request;
import io.core9.plugin.widgets.datahandler.ContextualDataHandler;
import io.core9.plugin.widgets.datahandler.DataHandler;
import io.core9.plugin.widgets.datahandler.DataHandlerDefaultConfig;
import io.core9.plugin.widgets.datahandler.DataHandlerFactoryConfig;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.PluginLoaded;
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
	
	//TODO: remove, create LineItemHandlers for delete, update etc.
	private CrudRepository<Coupon> coupons;
	
	@PluginLoaded
	public void onRepositoryFactory(RepositoryFactory factory) throws NoCollectionNamePresentException {
		coupons = factory.getRepository(Coupon.class);
	}
	
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
			deleteItemFromCart(request, cart, context);
			break;
		case PUT:
			updateItemInCart(cart, context);
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
	 * TODO: Move to LineItemHandlers
	 * @param request
	 * @param cart
	 * @param context
	 */
	public void deleteItemFromCart(Request request, Cart cart, Map<String, Object> context) {
		if(context.get("itemid") != null) {
			LineItem item = cart.getItems().get(context.get("itemid"));
			deleteItemFromCart(request, cart, item);
		}
	}
	
	
	/**
	 * TODO: Move to LineItemHandlers
	 * @param request
	 * @param cart
	 * @param item
	 */
	public void deleteItemFromCart(Request request, Cart cart, LineItem item) {
		if(item == null) {
			return;
		}
		if(item instanceof LinkedLineItem) {
			((LinkedLineItem) item).getLinkedLineItems().forEach((link) -> {
				deleteItemFromCart(request, cart, cart.getItems().remove(link));
			});
		}
		if(item instanceof CouponLineItem) {
			Coupon coupon = coupons.read(request.getVirtualHost(), item.getId());
			coupon.increment();
			coupons.update(request.getVirtualHost(), coupon.getId(), coupon);
		}
		cart.getItems().remove(item.getId());
	}
	
	/**
	 * TODO: Move to LineItemHandlers
	 * @param cart
	 * @param context
	 */
	public void updateItemInCart(Cart cart, Map<String, Object> context) {
		LineItem item = cart.getItems().get(context.get("itemid"));
		if(context.containsKey("quantity")) {
			int quantity = Integer.parseInt((String) context.get("quantity"));
			if(quantity > 0) {
				item.setQuantity(quantity);
			}
		}
	}

	/**
	 * TODO: Move to LineItemHandlers
	 * @param cart
	 * @param context
	 */
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
