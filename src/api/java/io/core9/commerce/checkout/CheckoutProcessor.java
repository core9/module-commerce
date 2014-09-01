package io.core9.commerce.checkout;

public interface CheckoutProcessor {

	void process(Order order);

	String getName();
}
