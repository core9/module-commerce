package io.core9.commerce.checkout;

import io.core9.commerce.CommerceDataHandlerHelper;
import io.core9.plugin.database.repository.CrudRepository;
import io.core9.plugin.database.repository.DataUtils;
import io.core9.plugin.database.repository.NoCollectionNamePresentException;
import io.core9.plugin.database.repository.RepositoryFactory;
import io.core9.plugin.server.VirtualHost;
import io.core9.plugin.server.request.Request;
import io.core9.plugin.widgets.datahandler.DataHandler;
import io.core9.plugin.widgets.datahandler.DataHandlerDefaultConfig;
import io.core9.plugin.widgets.datahandler.DataHandlerFactoryConfig;
import io.core9.plugin.widgets.widget.Widget;
import io.core9.plugin.widgets.widget.WidgetFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.events.PluginLoaded;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

@PluginImplementation
public class PaymentDataHandlerImpl<T extends DataHandlerDefaultConfig> implements PaymentDataHandler<T> {
	
	@InjectPlugin
	private CommerceDataHandlerHelper helper;
	
	@InjectPlugin
	private WidgetFactory widgets;

	private CrudRepository<PaymentMethod> methods;
	
	@PluginLoaded
	public void onRepositoryFactory(RepositoryFactory factory) throws NoCollectionNamePresentException {
		methods = factory.getRepository(PaymentMethod.class);
	}
	
	@Override
	public String getName() {
		return "Commerce-Payment";
	}

	@Override
	public Class<? extends DataHandlerFactoryConfig> getConfigClass() {
		return DataHandlerDefaultConfig.class;
	}

	@Override
	public DataHandler<T> createDataHandler(DataHandlerFactoryConfig options) {
		return new DataHandler<T>() {

			@Override
			public Map<String, Object> handle(Request req) {
				VirtualHost vhost = req.getVirtualHost();
				Order order = helper.getOrder(req);
				PaymentMethod method = getPaymentMethod(vhost, order);
				
				Map<String, Object> result = new HashMap<String, Object>(2);
				result.put("payment", DataUtils.toMap(method));
				DataHandler<?> handler = getPaymentDataHandler(vhost, method);
				if(handler != null) {
					order.setStatus("paying");
					helper.saveOrder(req, order);
					Map<String,Object> paymentData = handler.handle(req);
					result.put("paymentData", paymentData);
				} else {
					result.put("paymentData", new HashMap<String, Object>(0));
				}
				return result;
			}

			@SuppressWarnings("unchecked")
			@Override
			public T getOptions() {
				return (T) options;
			}
		};
	}
	
	private DataHandler<?> getPaymentDataHandler(VirtualHost vhost, PaymentMethod method) {
		if(method == null) {
			return null;
		}
		Widget widget = widgets.getRegistry(vhost).get(method.getWidget());
		if(widget == null) {
			return null;
		}
		return widget.getDataHandler();
	}
	
	private PaymentMethod getPaymentMethod(VirtualHost vhost, Order order) {
		Map<String, Object> query = new HashMap<String, Object>();
		query.put("name", order.getPaymentmethod());
		List<PaymentMethod> foundMethods = methods.query(vhost, query);
		if(foundMethods.size() == 1) {
			return foundMethods.get(0);
		}
		return null;
	}

}
