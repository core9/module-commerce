package io.core9.commerce.checkout.old;

import io.core9.commerce.checkout.Order;

public interface CheckoutProcessor {

	void process(Order order);

	String getName();
}
