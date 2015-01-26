package io.core9.commerce.cart.lineitem;

import java.util.Map;

import io.core9.commerce.cart.Cart;
import io.core9.commerce.cart.CartException;
import io.core9.plugin.server.request.Request;
import io.core9.plugin.server.request.RequestUtils;

public class MaximumQuantityLineItem extends StandardLineItem {
	
	private static final long serialVersionUID = -505937647311719407L;
	
	private int maximum;
	
	public MaximumQuantityLineItem() {
		
	}
	
	public MaximumQuantityLineItem(LineItem item) throws CartException {
		super(item);
	}
	
	public int getMaximum() {
		return this.maximum;
	}
	
	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}
	
	public void setQuantity(int quantity) throws CartException {
		if(maximum >= quantity) {
			super.setQuantity(quantity);
		} else {
			super.setQuantity(maximum);
			throw new CartException("We're sorry, you're only allowed to order %d items of this product.", maximum);
		}
	}
	
	@Override
	public boolean validates(Request req, Cart cart) {
		if(super.validates(req, cart)) {
			if(this.maximum >= this.getQuantity()) {
				return true;
			} else {
				RequestUtils.addMessage(req, "We're sorry, you're only allowed to order %d items of this product.", this.maximum);
			}
		}
		return false;
	}
	
	@Override
	public LineItem parse(Map<String, Object> context) throws NumberFormatException, CartException {
		if(context.get("max") != null) {
			this.maximum = Integer.parseInt((String) context.get("max"));
		}
		super.parse(context);
		return this;
	}

}
