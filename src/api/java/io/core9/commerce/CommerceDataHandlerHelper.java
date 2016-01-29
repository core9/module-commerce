package io.core9.commerce;

import io.core9.commerce.cart.Cart;
import io.core9.commerce.checkout.Order;
import io.core9.core.plugin.Core9Plugin;
import io.core9.module.auth.Session;
import io.core9.plugin.server.VirtualHost;
import io.core9.plugin.server.request.Request;

public interface CommerceDataHandlerHelper extends Core9Plugin {
	
	/**
	 * Returns a validated cart
	 * @param req
	 * @return
	 */
	Cart getCart(Request req);
	
	/**
	 * Saves a cart
	 * @param req
	 * @param cart
	 * @return
	 */
	Cart saveCart(Request req, Cart cart);
	
	/**
	 * Returns a validated order
	 * @param req
	 * @return
	 */
	Order getOrder(Request req);
	
	/**
	 * Returns a validated order
	 * @param req
	 * @param session
	 * @return
	 */
	Order getOrder(Request req, Session session);
	
	/**
	 * Returns an unvalidated order
	 * @param req
	 * @return
	 */
	Order getRawOrder(Request req);
	
	/**
	 * Save an order
	 * @param vhost
	 * @param order
	 * @return
	 */
	Order saveOrder(VirtualHost vhost, Order order);
	
	/**
	 * Save an order
	 * @param req
	 * @param order
	 * @return
	 */
	Order saveOrder(Request req, Order order);
	
	/**
	 * Create an order
	 * @param req
	 * @return
	 */
	Order createOrder(Request req);
	
	/**
	 * Finalize an order
	 * @param vhost
	 * @param order
	 * @return
	 */
	Order finalizeOrder(VirtualHost vhost, Order order);

	/**
	 * Finalize an order
	 * @param req
	 * @param order
	 * @return
	 */
	Order finalizeOrder(Request req, Order order);
	
	/**
	 * Create a new Payment ID
	 * @param req
	 * @return
	 */
	Order incrementPaymentCounter(Request req);
	
	/**
	 * Returns the session
	 * @param req
	 * @return
	 */
	Session getSession(Request req);
}
