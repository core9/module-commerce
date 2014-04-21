package io.core9.commerce.checkout;

import io.core9.commerce.cart.Cart;
import io.core9.commerce.payment.PaymentMethod;
import io.core9.module.auth.AuthenticationPlugin;
import io.core9.module.auth.Session;
import io.core9.plugin.database.repository.CrudRepository;
import io.core9.plugin.database.repository.DataUtils;
import io.core9.plugin.database.repository.NoCollectionNamePresentException;
import io.core9.plugin.database.repository.RepositoryFactory;
import io.core9.plugin.server.VirtualHost;
import io.core9.plugin.server.request.Request;
import io.core9.plugin.widgets.datahandler.DataHandler;
import io.core9.plugin.widgets.datahandler.DataHandlerFactoryConfig;
import io.core9.plugin.widgets.widget.Widget;
import io.core9.plugin.widgets.widget.WidgetFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.PluginLoaded;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@PluginImplementation
public class CheckoutDataHandlerImpl implements CheckoutDataHandler {
	
	@InjectPlugin
	private WidgetFactory widgets;
	
	@InjectPlugin
	private AuthenticationPlugin auth;
	
	private CrudRepository<PaymentMethod> methodsRepository;
	private CrudRepository<OrderImpl> orderRepository;
	
	private static final ObjectMapper MAPPER = new ObjectMapper(); 

	@PluginLoaded
	public void onRepositoryFactoryAvailable(RepositoryFactory factory) throws NoCollectionNamePresentException {
		methodsRepository = factory.getRepository(PaymentMethod.class);
		orderRepository = factory.getRepository(OrderImpl.class);
	}

	@Override
	public String getName() {
		return "Checkout";
	}

	@Override
	public Class<? extends DataHandlerFactoryConfig> getConfigClass() {
		return CheckoutDataHandlerConfig.class;
	}

	@Override
	public DataHandler<CheckoutDataHandlerConfig> createDataHandler(final DataHandlerFactoryConfig options) {
		return new DataHandler<CheckoutDataHandlerConfig>() {

			@Override
			public Map<String, Object> handle(Request req) {
				Map<String, Object> result = new HashMap<String, Object>();
				Session session = auth.getUser(req).getSession();
				switch(req.getMethod()) {
				case POST:
					handlePostedForm(session, req, ((CheckoutDataHandlerConfig) options).getNextStep());
					break;
				default:
					// Retrieve payment method information
					Map<String, PaymentMethod> paymentMethods = retrievePaymentMethodsForVhost(req.getVirtualHost());
					result.put("paymentmethods", MAPPER.convertValue(paymentMethods.values(), new TypeReference<List<Object>>() {}));

					// Retrieve order
					OrderImpl order = (OrderImpl) session.getAttribute("order");
					result.put("order", DataUtils.toMap(order));
					
					// Retrieve payment request options
					handlePaymentRequest(paymentMethods, req, order, result);
					break;
				}
				return result;
			}

			@Override
			public CheckoutDataHandlerConfig getOptions() {
				return (CheckoutDataHandlerConfig) options;
			}
		};
	}
	
	/**
	 * Handle the payment request
	 * Put the paymentData on the response object
	 * @param paymentMethods
	 * @param req
	 * @param order
	 * @param result
	 */
	private void handlePaymentRequest(Map<String, PaymentMethod> paymentMethods, Request req, OrderImpl order, Map<String,Object> result) {
		if(order != null && order.getPaymentmethod() != null) {
			PaymentMethod method = paymentMethods.get(order.getPaymentmethod());
			if(method == null) {
				return;
			}
			result.put("payment", MAPPER.convertValue(method, new TypeReference<Map<String,Object>>() {}));
			Widget widget = widgets.getRegistry(req.getVirtualHost()).get(method.getWidget());
			DataHandler<?> handler = widget.getDataHandler();
			if(handler != null) {
				result.put("paymentData", widget.getDataHandler().handle(req));
			} else {
				result.put("paymentData", "");
			}
		}
	}

	/**
	 * Retrieve all available payment methods
	 * @param vhost
	 * @return
	 */
	private Map<String, PaymentMethod> retrievePaymentMethodsForVhost(VirtualHost vhost) {
		Map<String, PaymentMethod> paymentMethods = new HashMap<String, PaymentMethod>();
		for(PaymentMethod method : methodsRepository.getAll(vhost)) {
			paymentMethods.put(method.getName(), method);
		}
		return paymentMethods;
	}

	/**
	 * Create an order and put it in the user session
	 * Redirect to the next step
	 * @param session
	 * @param req
	 * @param next
	 */
	private void handlePostedForm(Session session, Request req, String next) {
		OrderImpl order = null;
		if((order = (OrderImpl) session.getAttribute("order")) != null) {
			session.setAttribute("order", createOrder(order.getId(), req, session));
		} else {
			session.setAttribute("order", createOrder(null, req, session));
		}
		if(!next.equals(req.getPath())) {
			req.getResponse().sendRedirect(301, next);
		}
	}

	/**
	 * Create an order from the request body
	 * @param request
	 * @return
	 */
	private OrderImpl createOrder(String id, Request request, Session session) {
		Map<String,Object> form = new HashMap<String, Object>();
		for(Map.Entry<String,Object> entry : request.getBodyAsMap().entrySet()) {
			parseField(form, entry.getKey(), entry.getValue());
		}
		OrderImpl order = DataUtils.toObject(form, OrderImpl.class);
		Cart cart = (Cart) session.getAttribute("cart");
		order.setCart(cart);
		if(id == null || id != order.getId()) {
			orderRepository.create(request.getVirtualHost(), order);
		} else {
			order.setId(id);
			orderRepository.update(request.getVirtualHost(), id, order);
		}
		return order;
	}
	
	/**
	 * Parse a form request to a map
	 * @param result
	 * @param fieldname
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	private void parseField(Map<String,Object> result, String fieldname, Object value) {
		int start = fieldname.indexOf('[');
		int end = fieldname.indexOf(']');
		if(start != -1 && end != -1) {
			String field = fieldname.substring(0, start);
			if(!result.containsKey(field)) {
				result.put(field, new HashMap<String,Object>());
			}
			String innerfield = fieldname.substring(start + 1, end) + fieldname.substring(end + 1);
			parseField((Map<String, Object>) result.get(field), innerfield, value);
		} else {
			result.put(fieldname, value);
		}
	}
}
