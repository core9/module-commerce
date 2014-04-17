package io.core9.commerce.cart;

import io.core9.module.auth.AuthenticationPlugin;
import io.core9.module.auth.Session;
import io.core9.plugin.server.handler.Middleware;
import io.core9.plugin.server.request.Request;
import io.core9.plugin.server.vertx.VertxServer;

import java.util.ArrayList;
import java.util.Map;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.PluginLoaded;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

@PluginImplementation
public class CartActionsImpl implements CartActions {
	
	@InjectPlugin
	private AuthenticationPlugin auth;
	
	@PluginLoaded
	public void onServerLoaded(VertxServer server) {
		server.use("/core/cart(/:itemid)*", new Middleware() {
			@Override
			public void handle(Request request) {
				Session session = auth.getUser(request).getSession();
				Cart cart = (Cart) session.getAttribute("cart");
				if(cart == null) {
					cart = new CartImpl();
				}
				switch(request.getMethod()) {
				case POST:
					if(request.getParams().get("delete") != null) {
						deleteItemFromCart(cart, (String) request.getParams().get("itemid"));
					} else if (request.getParams().get("update") != null) {
						updateItemInCart(cart, request.getBodyAsMap());
					} else {
						addItemToCart(cart, request.getBodyAsMap());
					}
					break;
				case DELETE:
					deleteItemFromCart(cart, (String) request.getParams().get("itemid"));
					break;
				case PUT:
					updateItemInCart(cart, request.getBodyAsMap());
					break;
				default:
					break;
				}
				session.setAttribute("cart", cart);
				if(request.getBodyAsMap().get("redirect") != null) {
					request.getResponse().sendRedirect(301, (String) request.getBodyAsMap().get("redirect"));
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
	
	private void addItemToCart(Cart cart, Map<String, Object> bodyAsMap) {
		cart.addItem("ID", 12, 500, "Line item");
	}
	
	private void updateItemInCart(Cart cart, Map<String, Object> bodyAsMap) {
		LineItem item = cart.getItems().get(bodyAsMap.get("id"));
		if(bodyAsMap.containsKey("quantity")) {
			item.setQuantity(Integer.parseInt((String) bodyAsMap.get("quantity")));
		}
	}
}
