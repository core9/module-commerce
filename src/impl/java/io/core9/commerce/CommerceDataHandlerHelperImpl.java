package io.core9.commerce;

import io.core9.commerce.cart.Cart;
import io.core9.commerce.checkout.Order;
import io.core9.commerce.checkout.OrderImpl;
import io.core9.module.auth.AuthenticationPlugin;
import io.core9.module.auth.Session;
import io.core9.plugin.database.repository.CrudRepository;
import io.core9.plugin.database.repository.NoCollectionNamePresentException;
import io.core9.plugin.database.repository.RepositoryFactory;
import io.core9.plugin.server.VirtualHost;
import io.core9.plugin.server.request.Request;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.PluginLoaded;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

@PluginImplementation
public class CommerceDataHandlerHelperImpl implements CommerceDataHandlerHelper {
	
	public static final String CONTEXT_PREFIX    = "cdhh.";
	public static final String SESSION_CART_KEY  = "cart";
	public static final String SESSION_ORDER_KEY = "order";
	public static final String PATH_ORDER_ID     = "orderid";
	public static final String PATH_SESSION_ID   = "sessionid";
	private static final long ONE_WEEK_MILLIS    = 2678400000l;
	
	@InjectPlugin
	private AuthenticationPlugin auth;
	
	private CrudRepository<OrderImpl> orderRepository;
	
	@PluginLoaded
	public void onRepositoryFactoryAvailable(RepositoryFactory factory) throws NoCollectionNamePresentException {
		orderRepository = factory.getRepository(OrderImpl.class);
	}
	
	@Override
	public Cart getCart(Request req) {
		if(req.getContext(CONTEXT_PREFIX + SESSION_CART_KEY) != null) {
			return (Cart) req.getContext(CONTEXT_PREFIX + SESSION_CART_KEY);
		} else {
			Session session = auth.getUser(req).getSession();
			Cart cart = (Cart) session.getAttribute(SESSION_CART_KEY);
			if(cart == null) {
				cart = new Cart();
			}
			req.putContext(CONTEXT_PREFIX + SESSION_CART_KEY, cart);
			return cart;
		}
	}

	@Override
	public Cart saveCart(Request req, Cart cart) {
		Session session = auth.getUser(req).getSession();
		session.setAttribute(SESSION_CART_KEY, cart);
		session.setTimeout(ONE_WEEK_MILLIS);
		req.putContext(CONTEXT_PREFIX + SESSION_CART_KEY, cart);
		return cart;
	}

	@Override
	public Order getOrder(Request req) {
		if(req.getPathParams().containsKey(PATH_ORDER_ID) && req.getPathParams().containsKey(PATH_SESSION_ID)) {
			Order order = getOrder(req, req.getPathParams().get(PATH_ORDER_ID), req.getPathParams().get(PATH_SESSION_ID));
			setOnContext(req, order);
		}
		OrderImpl order = (OrderImpl) req.getContext(CONTEXT_PREFIX + SESSION_ORDER_KEY); 
		if(order == null) {
			Session session = auth.getUser(req).getSession();
			order = (OrderImpl) getOrder(req, session);
		}
		return order;
	}
	
	private void setOnContext(Request req, Order order) {
		saveCart(req, order.getCart());
		Session session = auth.getUser(req).getSession();
		req.putContext(CONTEXT_PREFIX + SESSION_ORDER_KEY, order);
		session.setAttribute(SESSION_ORDER_KEY, order);
		session.setTimeout(ONE_WEEK_MILLIS); //Timeout to one week
		req.putContext(CONTEXT_PREFIX + SESSION_ORDER_KEY, order);
	}
	
	/**
	 * Loads the order via sessionid and orderid
	 * @param req
	 * @param orderId
	 * @param sessionId
	 * @return
	 */
	private Order getOrder(Request req, String orderId, String sessionId) {
		Order order = orderRepository.read(req.getVirtualHost(), orderId);
		if(order != null && order.getSessionId().equals(sessionId)) {
			return order;
		}
		return null;
	}
	
	@Override
	public Order getOrder(Request req, Session session) {
		OrderImpl order = (OrderImpl) session.getAttribute(SESSION_ORDER_KEY);
		if(order == null) {
			order = (OrderImpl) createOrder(req);
		}
		order.setCart(getCart(req));
		req.putContext(CONTEXT_PREFIX + SESSION_ORDER_KEY, order);
		return order;
	}

	@Override
	public Order saveOrder(Request req, Order order) {
		Session session = auth.getUser(req).getSession();
		Order savedOrder = saveOrder(req.getVirtualHost(), order);
		session.setAttribute(SESSION_ORDER_KEY, savedOrder);
		session.setTimeout(ONE_WEEK_MILLIS); //Timeout to one week
		req.putContext(CONTEXT_PREFIX + SESSION_ORDER_KEY, savedOrder);
		return savedOrder;
	}
	
	@Override
	public Order saveOrder(VirtualHost vhost, Order order) {
		order.setTimestamp(System.currentTimeMillis() + "");
		return orderRepository.upsert(vhost, (OrderImpl) order);
	}

	@Override
	public Order createOrder(Request req) {
		OrderImpl order = new OrderImpl();
		order.setCart(getCart(req));
		order.setStatus("initialized");
		order.setSessionId(auth.getUser(req).getSession().getId());
		return saveOrder(req, order);
	}

	@Override
	public Order finalizeOrder(VirtualHost vhost, Order order) {
		order.setFinalized(true);
		Session session = auth.getSessionById(order.getSessionId());
		session.removeAttribute(SESSION_CART_KEY);
		session.removeAttribute(SESSION_ORDER_KEY);
		orderRepository.upsert(vhost, (OrderImpl) order);
		return order;
	}
	
	@Override
	public Order finalizeOrder(Request req, Order order) {
		order.setFinalized(true);
		saveOrder(req, order);
		Session session = auth.getUser(req).getSession();
		
		session.removeAttribute(SESSION_CART_KEY);
		session.removeAttribute(SESSION_ORDER_KEY);
		req.getContext().remove(CONTEXT_PREFIX + SESSION_CART_KEY);
		req.getContext().remove(CONTEXT_PREFIX + SESSION_ORDER_KEY);
		return null;
	}

	@Override
	public Order incrementPaymentCounter(Request req) {
		Order order = getOrder(req);
		order.incrementPaymentCounter();
		order = saveOrder(req, order);
		return order;
	}

	@Override
	public Session getSession(Request req) {
		return auth.getUser(req).getSession();
	}
	
}

