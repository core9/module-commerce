package io.core9.commerce;

import io.core9.commerce.cart.old.Cart;
import io.core9.core.plugin.Core9Plugin;
import io.core9.plugin.server.request.Request;

public interface CommerceDataHandlerHelper extends Core9Plugin {
	
	Cart getCart(Request req);
	
}
