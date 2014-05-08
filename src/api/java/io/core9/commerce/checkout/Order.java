package io.core9.commerce.checkout;

import io.core9.commerce.cart.Cart;

import java.util.Map;

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
	
	/**
	 * Return the payment data
	 * @return
	 */
	Map<String,Object> getPaymentData();
	
	/**
	 * Set the payment data
	 * @param data
	 */
	void setPaymentData(Map<String,Object> data);

	/**
	 * Returns true if the order is finalized
	 * @return
	 */
	boolean isFinalized();
	
	/**
	 * Set order finalization (customer cannot edit order anymore)
	 * @return
	 */
	void setFinalized(boolean finalized);
	
}
