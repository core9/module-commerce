package io.core9.commerce.checkout;

@Deprecated
public interface CheckoutProcessor {

	void process(Order order);

	String getName();
}
