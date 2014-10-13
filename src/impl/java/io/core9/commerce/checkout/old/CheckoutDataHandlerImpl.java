package io.core9.commerce.checkout.old;

import io.core9.commerce.cart.Cart;
import io.core9.commerce.checkout.old.CheckoutDataHandler;
import io.core9.commerce.checkout.old.CheckoutDataHandlerConfig;
import io.core9.commerce.checkout.old.CheckoutProcessor;
import io.core9.commerce.payment.old.PaymentMethod;
import io.core9.core.boot.CoreBootStrategy;
import io.core9.mail.MailerPlugin;
import io.core9.mail.MailerProfile;
import io.core9.module.auth.AuthenticationPlugin;
import io.core9.module.auth.Session;
import io.core9.plugin.database.repository.CrudRepository;
import io.core9.plugin.database.repository.DataUtils;
import io.core9.plugin.database.repository.NoCollectionNamePresentException;
import io.core9.plugin.database.repository.RepositoryFactory;
import io.core9.plugin.server.Cookie;
import io.core9.plugin.server.Server;
import io.core9.plugin.server.VirtualHost;
import io.core9.plugin.server.request.Request;
import io.core9.plugin.template.closure.ClosureTemplateEngine;
import io.core9.plugin.widgets.datahandler.DataHandler;
import io.core9.plugin.widgets.datahandler.DataHandlerFactoryConfig;
import io.core9.plugin.widgets.widget.Widget;
import io.core9.plugin.widgets.widget.WidgetFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import net.xeoh.plugins.base.Plugin;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.PluginLoaded;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

import org.apache.commons.lang3.ClassUtils;
import org.apache.log4j.Logger;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@PluginImplementation
public class CheckoutDataHandlerImpl extends CoreBootStrategy implements CheckoutDataHandler {
	
	private static final Logger LOG = Logger.getLogger(CheckoutDataHandlerImpl.class);
	private static final Map<String, CheckoutProcessor> PROCESSORS = new HashMap<String, CheckoutProcessor>();
	
	@InjectPlugin
	private WidgetFactory widgets;
	
	@InjectPlugin
	private AuthenticationPlugin auth;
	
	@InjectPlugin
	private MailerPlugin mailer;
	
	@InjectPlugin
	private ClosureTemplateEngine engine;
	
	@InjectPlugin
	private Server server;
	
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
			CheckoutDataHandlerConfig config = (CheckoutDataHandlerConfig) options;

			@Override
			public Map<String, Object> handle(Request req) {
				Map<String, Object> result = new HashMap<String, Object>();
				//FIXME QUICK AND DIRTY OGONE ERROR FIX
				Session session = null;
				if(req.getParams().get("COMPLUS") != null && req.getCookie("CORE9SESSIONID") == null) {
					Cookie cookie = server.newCookie("CORE9SESSIONID");
					cookie.setValue((String) req.getParams().get("COMPLUS"));
					session = auth.getUser(req, cookie).getSession();
				} else {
					session = auth.getUser(req).getSession();
				}
				switch(req.getMethod()) {
				case POST:
					handlePostedForm(session, req, config.getNextStep());
					break;
				default:
					// Retrieve payment method information
					Map<String, PaymentMethod> paymentMethods = retrievePaymentMethodsForVhost(req.getVirtualHost());
					result.put("paymentmethods", MAPPER.convertValue(paymentMethods.values(), new TypeReference<List<Object>>() {}));
					
					// Retrieve order
					OrderImpl order = (OrderImpl) session.getAttribute("order");
					
					if(order != null) {
						String orderId = order.getId(); // HAS TO BE CALLED TO GENERATE ID
						System.out.println(orderId);
						Cart cart = (Cart) session.getAttribute("cart");
						order.setCart(cart);
					}
					
					// Handle processors
					if(config.getProcessors() != null && order != null) {
						for(String processor : config.getProcessors()) {
							PROCESSORS.get(processor).process(order);
						}
					}
					
					session.setAttribute("order", order);
					result.put("order", DataUtils.toMap(order));
					
					// Retrieve payment request options
					handlePaymentRequest(paymentMethods, req, order, result);
					if(order != null && order.getId() != null) {
						orderRepository.upsert(req.getVirtualHost(), order);
					}
					break;
				}
				if(config.getDestroySession()) {
					// Retrieve order
					OrderImpl order = (OrderImpl) session.getAttribute("order");
					order.setFinalized(true);
					orderRepository.upsert(req.getVirtualHost(), order);
					session.removeAttribute("order");
					session.removeAttribute("cart");
					try {
						MailerProfile profile = mailer.getProfile(req.getVirtualHost(), config.getMailerProfile());
						MimeMessage message = (MimeMessage) mailer.create(profile);
						message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(order.getBilling().getEmail()));
						message.setFrom(new InternetAddress(config.getMailerFromAddress()));
						message.setSubject(config.getMailerSubject());
						message.setText(engine.render(req.getVirtualHost(), config.getMailerTemplate(), DataUtils.toMap(order)), "utf-8", "html");
						mailer.send(profile, message);
					} catch (MessagingException e) {
						LOG.error("Error sending mail: " + e.getMessage());
						e.printStackTrace();
					}
				}
				return result;
			}

			@Override
			public CheckoutDataHandlerConfig getOptions() {
				return config;
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
			DataHandler<?> handler = null;
			if(widget != null) {
				handler = widget.getDataHandler();;
			}
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
		// Retrieve order
		OrderImpl order = (OrderImpl) session.getAttribute("order");
		if(order != null) {
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
		for(Map.Entry<String,Object> entry : request.getBodyAsMap().toBlocking().last().entrySet()) {
			parseField(form, entry.getKey(), entry.getValue());
		}
		OrderImpl order = DataUtils.toObject(form, OrderImpl.class);
		Cart cart = (Cart) session.getAttribute("cart");
		order.setCart(cart);
		if(id == null) {
			id = order.getId();
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

	@Override
	public void processPlugins() {
		for (Plugin plugin : this.registry.getPlugins()) {
			List<Class<?>> interfaces = ClassUtils.getAllInterfaces(plugin.getClass());
			if (interfaces.contains(CheckoutProcessor.class)) {
				CheckoutProcessor processor = (CheckoutProcessor) plugin;
				PROCESSORS.put(processor.getName(), processor);
				System.out.println("Found admin plugin." + processor.getName());
			}
		}

	}

	@Override
	public Integer getPriority() {
		return 20130;
	}
}
