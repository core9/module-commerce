package io.core9.commerce.checkout;

import io.core9.commerce.CommerceDataHandlerHelper;
import io.core9.mail.MailerPlugin;
import io.core9.mail.MailerProfile;
import io.core9.plugin.database.repository.DataUtils;
import io.core9.plugin.server.request.Request;
import io.core9.plugin.template.closure.ClosureTemplateEngine;
import io.core9.plugin.widgets.datahandler.DataHandler;
import io.core9.plugin.widgets.datahandler.DataHandlerFactoryConfig;

import java.util.HashMap;
import java.util.Map;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

import org.apache.log4j.Logger;

@PluginImplementation
public class OrderFinalizerDataHandlerImpl<T extends OrderFinalizerDataHandlerConfig> implements OrderFinalizerDataHandler<T> {
	
	private static final Logger LOG = Logger.getLogger(OrderFinalizerDataHandler.class);
	
	@InjectPlugin
	private CommerceDataHandlerHelper helper;
	
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
				Order order = helper.getOrder(req);
				switch (order.getStatus()) {
				case "initalized":
				case "paid":
					helper.finalizeOrder(req, order);
					mail(config, req, order);
					break;
				default:
					LOG.error("Order " + order.getId() + " entered finalizing page without paid/initialized status");
					break;
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

	protected void mail(T config, Request req, Order order) {
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

}
