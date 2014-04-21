package io.core9.commerce.checkout;

import io.core9.commerce.cart.Cart;

public interface Order {
	
	/**
	 * Return the Order ID
	 * @return
	 */
	String getId();
	
	/**
	 * Set the Order ID
	 * @param id
	 */
	void setId(String id);
	
	/**
	 * Return the Cart associated with the order
	 * @return
	 */
	Cart getCart();
	
	/**
	 * Set the Cart associated with the order
	 * @param cart
	 */
	void setCart(Cart cart);
}
