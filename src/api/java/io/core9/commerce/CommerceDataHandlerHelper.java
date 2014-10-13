package io.core9.commerce;

import io.core9.commerce.cart.Cart;
import io.core9.core.plugin.Core9Plugin;
import io.core9.plugin.server.request.Request;

public interface CommerceDataHandlerHelper extends Core9Plugin {
	
	Cart getCart(Request req);
	
	void saveCart(Request req, Cart cart);
}
