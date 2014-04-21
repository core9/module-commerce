package io.core9.commerce.checkout;

import io.core9.commerce.cart.Cart;

public interface Order {
	Cart getCart();
	void setCart(Cart cart);
}
