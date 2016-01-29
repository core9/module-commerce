package io.core9.commerce;

import java.util.Map;

import io.core9.commerce.checkout.Order;
import io.core9.core.plugin.Core9Plugin;
import io.core9.plugin.server.request.Request;

public interface CommercePaymentHelper extends Core9Plugin {

	Map<String, Object> verifyPayment(Request req, Order order);

}
