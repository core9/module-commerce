package io.core9.commerce.checkout;

import io.core9.commerce.payment.PaymentMethod;
import io.core9.module.auth.AuthenticationPlugin;
import io.core9.module.auth.Session;
import io.core9.plugin.database.repository.CrudRepository;
import io.core9.plugin.database.repository.NoCollectionNamePresentException;
import io.core9.plugin.database.repository.RepositoryFactory;
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
	
	private static final ObjectMapper MAPPER = new ObjectMapper(); 

	@PluginLoaded
	public void onRepositoryFactoryAvailable(RepositoryFactory factory) throws NoCollectionNamePresentException {
		methodsRepository = factory.getRepository(PaymentMethod.class);
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
		final String next = ((CheckoutDataHandlerConfig) options).getNextStep();
		return new DataHandler<CheckoutDataHandlerConfig>() {

			@Override
			public Map<String, Object> handle(Request req) {
				Map<String, Object> result = new HashMap<String, Object>();
				Session session = auth.getUser(req).getSession();
				switch(req.getMethod()) {
				case POST:
					session.setAttribute("order", createOrder(req));
					if(!next.equals(req.getPath())) {
						req.getResponse().sendRedirect(301, next);
					}
					break;
				default:
					Map<String, PaymentMethod> paymentMethods = new HashMap<String, PaymentMethod>();
					for(PaymentMethod method : methodsRepository.getAll(req.getVirtualHost())) {
						paymentMethods.put(method.getName(), method);
					}
					result.put("paymentmethods", MAPPER.convertValue(paymentMethods.values(), new TypeReference<List<Object>>() {}));

					@SuppressWarnings("unchecked")
					Map<String,Object> order = (Map<String, Object>) session.getAttribute("order");
					result.put("order", order);
					if(order != null && order.get("paymentmethod") != null) {
						PaymentMethod method = paymentMethods.get(order.get("paymentmethod"));
						if(method == null) {
							return result;
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
	
	private Map<String,Object> createOrder(Request request) {
		Map<String,Object> form = new HashMap<String, Object>();
		for(Map.Entry<String,Object> entry : request.getBodyAsMap().entrySet()) {
			parseField(form, entry.getKey(), entry.getValue());
		}
		return form;
	}
	
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
