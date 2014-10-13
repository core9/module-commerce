package io.core9.commerce.checkout;

import io.core9.commerce.checkout.old.FeedbackDataHandler;
import io.core9.commerce.checkout.old.FeedbackDataHandlerConfig;
import io.core9.commerce.payment.old.PaymentMethod;
import io.core9.module.auth.AuthenticationPlugin;
import io.core9.module.auth.Session;
import io.core9.plugin.database.repository.CrudRepository;
import io.core9.plugin.database.repository.NoCollectionNamePresentException;
import io.core9.plugin.database.repository.RepositoryFactory;
import io.core9.plugin.server.VirtualHost;
import io.core9.plugin.server.request.Request;
import io.core9.plugin.widgets.datahandler.DataHandler;
import io.core9.plugin.widgets.datahandler.DataHandlerFactoryConfig;
import io.core9.plugin.widgets.widget.Widget;
import io.core9.plugin.widgets.widget.WidgetFactory;

import java.util.HashMap;
import java.util.Map;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.PluginLoaded;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

/**
 * The FeedbackDataHandlerImpl datahandler is used for the automatic processing of orders.
 * An order is looked up by it's ID in the params, the param key can be specified.
 * 
 * This datahandler is mostly used for payment processing feedback mechanisms.
 * It will load and unload the order to the session by it's order id.
 * 
 * The output of the datahandler can be a map containing the error, or the feedback from the payment datahandler.
 * This information should however not be printed on the page, as it might involve private information.
 * @author mark
 *
 */
@PluginImplementation
public class FeedbackDataHandlerImpl implements FeedbackDataHandler {
	
	@InjectPlugin
	private WidgetFactory widgets;
	
	@InjectPlugin
	private AuthenticationPlugin auth;
	
	private CrudRepository<PaymentMethod> methodsRepository;
	private CrudRepository<OrderImpl> orderRepository;
	
	@PluginLoaded
	public void onRepositoryFactoryAvailable(RepositoryFactory factory) throws NoCollectionNamePresentException {
		methodsRepository = factory.getRepository(PaymentMethod.class);
		orderRepository = factory.getRepository(OrderImpl.class);
	}

	@Override
	public String getName() {
		return "Checkout-Feedback";
	}

	@Override
	public Class<? extends DataHandlerFactoryConfig> getConfigClass() {
		return FeedbackDataHandlerConfig.class;
	}

	@Override
	public DataHandler<FeedbackDataHandlerConfig> createDataHandler(DataHandlerFactoryConfig options) {
		final FeedbackDataHandlerConfig config = (FeedbackDataHandlerConfig) options;
		return new DataHandler<FeedbackDataHandlerConfig> () {

			@Override
			public Map<String, Object> handle(Request req) {
				boolean orderAlreadyOnSession = true;
				Map<String, Object> result = new HashMap<String, Object>();
				String orderId = null;
				for(String key : config.getOrderIdentifiers()) {
					if(req.getParams().get(key) != null) {
						orderId = (String) req.getParams().get(key);
					}
				}
				if(orderId == null) {
					result.put("error", "No order id found in the parameters, cannot process the request.");
					return result;
				}
				Session session = auth.getUser(req).getSession();
				// Retrieve order
				OrderImpl order = (OrderImpl) session.getAttribute("order");
				if(order != null && !order.getId().equals(orderId)) {
					result.put("error", "You are processing a different order number, cannot process the request.");
					return result;
				}
				if(order == null) {
					orderAlreadyOnSession = false;
					order = orderRepository.read(req.getVirtualHost(), orderId);
					if(order == null) {
						result.put("error", "Your order isn't known, cannot process the request.");
						return result;
					} else {
						session.setAttribute("order", order);
					}
				}
				if(order != null && order.getPaymentmethod() != null) {
					PaymentMethod method = getPaymentMethod(req.getVirtualHost(), order.getPaymentmethod());
					Widget widget = widgets.getRegistry(req.getVirtualHost()).get(method.getWidget());
					DataHandler<?> handler = widget.getDataHandler();
					if(handler != null) {
						result.put("paymentData", widget.getDataHandler().handle(req));
						OrderImpl updated = (OrderImpl) session.getAttribute("order");
						orderRepository.update(req.getVirtualHost(), updated.getId(), updated);
					} else {
						result.put("paymentData", "");
					}
				}
				if(config.getNextStep() != null && orderAlreadyOnSession) {
					req.getResponse().sendRedirect(301, config.getNextStep());
				}
				if(!orderAlreadyOnSession) {
					session.removeAttribute("order");
				}
				return result;
			}

			@Override
			public FeedbackDataHandlerConfig getOptions() {
				return config;
			}
			
		};
	}
	
	/**
	 * Retrieve payment method
	 * @param vhost
	 * @return
	 */
	private PaymentMethod getPaymentMethod(VirtualHost vhost, String name) {
		for(PaymentMethod method : methodsRepository.getAll(vhost)) {
			if(method.getName().equals(name)) {
				return method;
			}
		}
		return null;
	}

}
