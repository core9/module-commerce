package io.core9.commerce;

import io.core9.commerce.cart.Cart;
import io.core9.commerce.checkout.Order;
import io.core9.commerce.checkout.OrderImpl;
import io.core9.module.auth.AuthenticationPlugin;
import io.core9.module.auth.Session;
import io.core9.plugin.database.repository.CrudRepository;
import io.core9.plugin.database.repository.NoCollectionNamePresentException;
import io.core9.plugin.database.repository.RepositoryFactory;
import io.core9.plugin.server.request.Request;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.PluginLoaded;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

@PluginImplementation
public class CommerceDataHandlerHelperImpl implements CommerceDataHandlerHelper {
	
	public static final String SESSION_CART_KEY  = "cart";
	public static final String SESSION_ORDER_KEY = "order";
	
	@InjectPlugin
	private AuthenticationPlugin auth;
	
	private CrudRepository<OrderImpl> orderRepository;
	
	@PluginLoaded
	public void onRepositoryFactoryAvailable(RepositoryFactory factory) throws NoCollectionNamePresentException {
		orderRepository = factory.getRepository(OrderImpl.class);
	}

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
	public Cart saveCart(Request req, Cart cart) {
		Session session = auth.getUser(req).getSession();
		session.setAttribute(SESSION_CART_KEY, cart);
		return cart;
	}

	@Override
	public Order getOrder(Request req) {
		Session session = auth.getUser(req).getSession();
		OrderImpl order = (OrderImpl) session.getAttribute("order");
		if(order == null) {
			order = (OrderImpl) createOrder(req);
		} else {
			order.setCart(getCart(req));
		}
		return order;
	}

	@Override
	public Order saveOrder(Request req, Order order) {
		Session session = auth.getUser(req).getSession();
		session.setAttribute(SESSION_ORDER_KEY, order);
		return orderRepository.upsert(req.getVirtualHost(), (OrderImpl) order);
	}

	@Override
	public Order createOrder(Request req) {
		OrderImpl order = new OrderImpl();
		order.setCart(getCart(req));
		order.setStatus("initialized");
		return saveOrder(req, order);
	}

	@Override
	public Order finalizeOrder(Request req, Order order) {
		order.setFinalized(true);
		saveOrder(req, order);
		Session session = auth.getUser(req).getSession();
		session.removeAttribute(SESSION_CART_KEY);
		session.removeAttribute(SESSION_ORDER_KEY);
		return null;
	}

}
