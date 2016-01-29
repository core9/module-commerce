package io.core9.commerce.checkout;

import java.util.HashMap;
import java.util.Map;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import io.core9.commerce.CommerceDataHandlerHelper;
import io.core9.commerce.CommercePaymentHelper;
import io.core9.mail.MailerPlugin;
import io.core9.mail.MailerProfile;
import io.core9.plugin.database.repository.DataUtils;
import io.core9.plugin.server.VirtualHost;
import io.core9.plugin.server.request.Request;
import io.core9.plugin.template.closure.ClosureTemplateEngine;
import io.core9.plugin.widgets.datahandler.DataHandler;
import io.core9.plugin.widgets.datahandler.DataHandlerFactoryConfig;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

@PluginImplementation
public class OrderFinalizerDataHandlerImpl<T extends OrderFinalizerDataHandlerConfig> implements OrderFinalizerDataHandler<T> {
	
	private static final Logger LOG = Logger.getLogger(OrderFinalizerDataHandler.class);
	
	@InjectPlugin
	private CommerceDataHandlerHelper helper;
	
	@InjectPlugin
	private CommercePaymentHelper payment;
	
	@InjectPlugin
	private MailerPlugin mailer;
	
	@InjectPlugin
	private ClosureTemplateEngine engine;

	@Override
	public String getName() {
		return "Commerce-OrderFinalize";
	}

	@Override
	public Class<? extends DataHandlerFactoryConfig> getConfigClass() {
		return OrderFinalizerDataHandlerConfig.class;
	}

	@Override
	public DataHandler<T> createDataHandler(DataHandlerFactoryConfig options) {
		@SuppressWarnings("unchecked")
		final T config = (T) options;
		return new DataHandler<T>() {

			@Override
			public Map<String, Object> handle(Request req) {
				Map<String,Object> result = new HashMap<String, Object>(2);
				Order order = helper.getRawOrder(req);
				if(order.getStatus().equals("paying") || order.getStatus().equals("paid") || order.getStatus().equals("uncertain") || order.getStatus().equals("failed")) {
					if(canBeFinalized(order)) {
						result.put("verifier", payment.verifyPayment(req, order));
						finalizeOrder(req, req.getVirtualHost(), order, config);
					} else {
						order.setStatus("BLOCK");
						order.setMessage("Some of the cart/billing/shipping fields aren't entered correctly.");
					}
				} else {
					order.setStatus("BLOCK");
					order.setMessage("Your order has the wrong status, you cannot finalize your order.");
					LOG.error("Order " + order.getId() + " entered finalizing page without paying status");
				}
				if(order != null) {
					result.put("order", DataUtils.toMap(order));
				}
				return result;
			}

			@Override
			public T getOptions() {
				return config;
			}
		};
	}
	
	@Override
	public boolean canBeFinalized(Order order) {
		return order.getCart() != null &&
				   order.getBilling() != null &&
				   order.getShipping() != null &&
				   order.getPaymentmethod() != null;
	}
	
	@Override
	public void finalizeOrder(Request req, VirtualHost vhost, Order order, T config) {
		if(req != null) {
			helper.finalizeOrder(req, order);
		} else {
			helper.finalizeOrder(vhost, order);
		}
		sendOrderMail(config, vhost, order, config.getMailerTemplate());
	}
	
	@Override
	public void sendOrderMail(T config, VirtualHost vhost, Order order, String templateName) {
		try {
			MailerProfile profile = mailer.getProfile(vhost, config.getMailerProfile());
			MimeMessage message = (MimeMessage) mailer.create(profile);
			message.addRecipients(Message.RecipientType.TO, InternetAddress.parse(order.getBilling().getEmail()));
			message.setFrom(new InternetAddress(getOrDefault("from", config.getMailerFromAddress(), profile)));
			message.setSubject(getOrDefault("subject", config.getMailerSubject(), profile));
			message.setText(engine.render(vhost, templateName, DataUtils.toMap(order)), "utf-8", "html");
			mailer.send(profile, message);
		} catch (MessagingException e) {
			LOG.error("Error sending mail: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	protected String getOrDefault(String type, String entered, MailerProfile profile) {
		if(entered == null || entered.equals("")) {
			return (String) profile.getDefaultSettings().get(type);
		}
		return entered;
	}

}
