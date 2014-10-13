package io.core9.commerce;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;
import io.core9.commerce.cart.old.Cart;
import io.core9.module.auth.AuthenticationPlugin;
import io.core9.module.auth.Session;
import io.core9.plugin.server.request.Request;

@PluginImplementation
public class CommerceDataHandlerHelperImpl implements CommerceDataHandlerHelper {
	
	@InjectPlugin
	private AuthenticationPlugin auth;

	@Override
	public Cart getCart(Request req) {
		Session session = auth.getUser(req).getSession();
		Cart cart = (Cart) session.getAttribute("cart");
		if(cart == null) {
			cart = new Cart();
		}
		return cart;
	}

}
