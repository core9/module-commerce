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
		
	@InjectPlugin
	private CommercePaymentHelper payment;

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
			cart = validateCart(cart, req, session);
			if(cart == null) {
				cart = new Cart();
			}
			req.putContext(CONTEXT_PREFIX + SESSION_CART_KEY, cart);
			return cart;
		}
	}

	/**
	 * Validates the cart (checks if order isn't already completed)
	 * @param cart
	 * @param session
	 * @return
	 */
	private Cart validateCart(Cart cart, Request req, Session session) {
		Order order = (OrderImpl) session.getAttribute(SESSION_ORDER_KEY);
		if(order != null && order.getStatus().equals("paying")) {
			order = validateOrder(order, req);
			if(order == null || order.getStatus().equals("paid")) {
				removeCart(req, session);
				return null;
			} else {
				return cart;
			}
		}
		return cart;
	}

	/**
	 * Remove the cart from the session
	 * @param session
	 */
	private void removeCart(Request req, Session session) {
		session.removeAttribute(SESSION_CART_KEY);
		req.getContext().remove(CONTEXT_PREFIX + SESSION_CART_KEY);
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
	public Order getRawOrder(Request req) {
		Order order = getOrderFromRequest(req);
		if(order == null) {
			Session session = auth.getUser(req).getSession();
			order = (OrderImpl) getOrderFromSession(session);
		}
		if(order == null) {
			order = (OrderImpl) createOrder(req);
			setOnContext(req, order);
		}
		order.setCart(getRawCart(req));
		if(order.getSessionId() == null) {
			order.setSessionId(auth.getUser(req).getSession().getId());
			saveOrder(req, order);
		}
		return order;
	}
	
	private Cart getRawCart(Request req) {
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
	public Order getOrder(Request req) {
		Order order = getOrderFromRequest(req);
		if(order == null) {
			Session session = auth.getUser(req).getSession();
			order = (OrderImpl) getOrderFromSession(session);
		}
		order = validateOrder(order, req);
		if(order == null) {
			order = (OrderImpl) createOrder(req);
			setOnContext(req, order);
		}
		order.setCart(getCart(req));
		if(order.getSessionId() == null) {
			order.setSessionId(auth.getUser(req).getSession().getId());
			saveOrder(req, order);
		}
		return order;
	}
	
	@Override
	public Order getOrder(Request req, Session session) {
		Order order = getOrderFromSession(session);
		if(order == null) {
			order = (OrderImpl) createOrder(req);
		}
		order.setCart(getCart(req));
		if(order.getSessionId() == null) {
			order.setSessionId(auth.getUser(req).getSession().getId());
			saveOrder(req, order);
		}
		setOnContext(req, order);
		return order;
	}
	
	/**
	 * Loads the order via sessionid and orderid
	 * @param req
	 * @param orderId
	 * @param sessionId
	 * @return
	 */
	private Order getOrderFromRepository(Request req, String orderId, String sessionId) {
		Order order = orderRepository.read(req.getVirtualHost(), orderId);
		if(order != null && order.getSessionId().equals(sessionId)) {
			return order;
		}
		return null;
	}
	
	private Order getOrderFromSession(Session session) {
		Order order = (OrderImpl) session.getAttribute(SESSION_ORDER_KEY);
		return order;
	}
	
	private Order getOrderFromRequest(Request req) {
		if(req.getPathParams().containsKey(PATH_ORDER_ID) && req.getPathParams().containsKey(PATH_SESSION_ID)) {
			Order order = getOrderFromRepository(req, req.getPathParams().get(PATH_ORDER_ID), req.getPathParams().get(PATH_SESSION_ID));
			setOnContext(req, order);
		}
		OrderImpl order = (OrderImpl) req.getContext(CONTEXT_PREFIX + SESSION_ORDER_KEY);
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
		orderRepository.upsert(vhost, (OrderImpl) order);
		return order;
	}
	
	@Override
	public Order finalizeOrder(Request req, Order order) {
		order.setFinalized(true);
		saveOrder(req, order);
		Session session = auth.getUser(req).getSession();
		removeCart(req, session);
		removeOrderFromContext(req);
		return null;
	}

	/**
	 * Checks if the order is valid
	 * @param req
	 * @param session
	 */
	private Order validateOrder(Order order, Request req) {
		if(order == null) {
			return null;
		} else if(order.getStatus().equals("paying")) {
			payment.verifyPayment(req, order);
			order = orderRepository.read(req.getVirtualHost(), order.getId());
			if(order.getStatus().equals("paid")) {
				removeOrderFromContext(req);
				return null;
			}	
		}
		return order;
	}
	
	/**
	 * Remove the order from the session
	 * @param req
	 * @param session
	 */
	private void removeOrderFromContext(Request req) {
		Session session = auth.getUser(req).getSession();
		session.removeAttribute(SESSION_ORDER_KEY);
		req.getContext().remove(CONTEXT_PREFIX + SESSION_ORDER_KEY);		
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

