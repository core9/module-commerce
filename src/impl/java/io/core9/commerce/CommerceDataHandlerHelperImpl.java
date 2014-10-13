package io.core9.commerce;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;
import io.core9.commerce.cart.Cart;
import io.core9.module.auth.AuthenticationPlugin;
import io.core9.module.auth.Session;
import io.core9.plugin.server.request.Request;

@PluginImplementation
public class CommerceDataHandlerHelperImpl implements CommerceDataHandlerHelper {
	
	public static final String SESSION_CART_KEY = "cart";
	
	@InjectPlugin
	private AuthenticationPlugin auth;

	@Override
	public Cart getCart(Request req) {
		Session session = auth.getUser(req).getSession();
		Cart cart = (Cart) session.getAttribute(SESSION_CART_KEY);
		if(cart == null) {
			cart = new Cart();
		}
		return cart;
	}

	@Override
	public void saveCart(Request req, Cart cart) {
		Session session = auth.getUser(req).getSession();
		session.setAttribute(SESSION_CART_KEY, cart);
	}

}
