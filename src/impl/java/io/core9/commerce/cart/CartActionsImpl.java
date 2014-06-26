package io.core9.commerce.cart;

import io.core9.module.auth.AuthenticationPlugin;
import io.core9.module.auth.Session;
import io.core9.plugin.server.Server;
import io.core9.plugin.server.handler.Middleware;
import io.core9.plugin.server.request.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.PluginLoaded;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

@PluginImplementation
public class CartActionsImpl implements CartActions {
	
	@InjectPlugin
	private AuthenticationPlugin auth;
	
	@InjectPlugin
	private CommerceEncryptionPlugin encryption;
	
	@PluginLoaded
	public void onServerLoaded(Server server) {
		server.use("/core/cart(/:itemid)*", new Middleware() {
			@Override
			public void handle(Request request) {
				Map<String,Object> requestBody = request.getBodyAsMap().toBlocking().last();
				Session session = auth.getUser(request).getSession();
				Cart cart = (Cart) session.getAttribute("cart");
				if(cart == null) {
					cart = new Cart();
				}
				switch(request.getMethod()) {
				case POST:
					if(request.getParams().get("delete") != null) {
						deleteItemFromCart(cart, (String) request.getParams().get("itemid"));
					} else if (request.getParams().get("update") != null) {
						updateItemInCart(cart, requestBody);
					} else {
						addItemToCart(cart, (String) request.getParams().get("itemid"), requestBody);
					}
					break;
				case DELETE:
					deleteItemFromCart(cart, (String) request.getParams().get("itemid"));
					break;
				case PUT:
					updateItemInCart(cart, requestBody);
					break;
				default:
					break;
				}
				session.setAttribute("cart", cart);
				if(requestBody.get("redirect") != null) {
					request.getResponse().sendRedirect(301, (String) requestBody.get("redirect"));
				} else {
					request.getResponse().sendJsonArray(new ArrayList<LineItem>(cart.getItems().values()));
				}
			}
		});
	}

	private void deleteItemFromCart(Cart cart, String itemId) {
		if(itemId != null) {
			cart.getItems().remove(itemId);
		}
	}
	
	/**
	 * Add an item to the cart
	 * TODO: Make dynamic lineItemTypes
	 * @param cart
	 * @param itemId
	 * @param bodyAsMap
	 */
	private void addItemToCart(Cart cart, String itemId, Map<String, Object> bodyAsMap) {
		int quantity = Integer.parseInt((String) bodyAsMap.get("quantity"));
		if(quantity > 0 && isValid(bodyAsMap)) {
			cart.addItem(
				itemId,
				quantity, 
				Integer.parseInt((String) bodyAsMap.get("price")), 
				(String) bodyAsMap.get("description"),
				(String) bodyAsMap.get("image"),
				(String) bodyAsMap.get("link"));
		}
	}
	
	private void updateItemInCart(Cart cart, Map<String, Object> bodyAsMap) {
		LineItem item = cart.getItems().get(bodyAsMap.get("id"));
		if(bodyAsMap.containsKey("quantity")) {
			int quantity = Integer.parseInt((String) bodyAsMap.get("quantity"));
			if(quantity > 0) {
				item.setQuantity(quantity);
			}
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
