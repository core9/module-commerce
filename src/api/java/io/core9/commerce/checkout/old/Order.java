package io.core9.commerce.checkout.old;

import io.core9.commerce.cart.Cart;
import io.core9.commerce.cart.LineItem;

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

	/**
	 * Get the shipping costs for an order
	 * @return
	 */
	LineItem getShippingCost();
	
	/**
	 * Set the shipping costs for an order
	 * @return
	 */
	void setShippingCost(LineItem shippingCost);
	
	/**
	 * Get the order total
	 * @return
	 */
	int getTotal();
	
	/**
	 * Set an order remark
	 * @param remark
	 */
	void setRemark(String remark);
	
	/**
	 * Get the order remarks
	 * @return
	 */
	String getRemark();
	
	/**
	 * Set an optional order status	
	 * @param status
	 */
	void setStatus(String status);
	
	/**
	 * Return the order status
	 * @return
	 */
	String getStatus();
	
	/**
	 * Set an optional order message
	 * @param message
	 */
	void setMessage(String message);
	
	/**
	 * Get the order message
	 * @return
	 */
	String getMessage();
}
