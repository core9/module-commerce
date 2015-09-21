package io.core9.commerce;

import io.core9.commerce.cart.Cart;
import io.core9.commerce.checkout.Order;
import io.core9.core.plugin.Core9Plugin;
import io.core9.module.auth.Session;
import io.core9.plugin.server.request.Request;

public interface CommerceDataHandlerHelper extends Core9Plugin {
	
	Cart getCart(Request req);
	
	Cart saveCart(Request req, Cart cart);
	
	Order getOrder(Request req);
	
	Order saveOrder(Request req, Order order);
	
	Order createOrder(Request req);

	Order finalizeOrder(Request req, Order order);

	Order getOrder(Request req, Session session);
	
	Order renewOrderID(Request req);
}
