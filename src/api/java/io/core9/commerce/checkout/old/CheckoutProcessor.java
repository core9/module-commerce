package io.core9.commerce.checkout.old;

public interface CheckoutProcessor {

	void process(Order order);

	String getName();
}
