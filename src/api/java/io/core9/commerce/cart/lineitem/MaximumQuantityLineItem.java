package io.core9.commerce.cart.lineitem;

import java.util.Map;

import io.core9.commerce.cart.Cart;
import io.core9.plugin.server.request.Request;

public class MaximumQuantityLineItem extends StandardLineItem {
	
	private int maximum;
	private static final long serialVersionUID = -505937647311719407L;
	
	public MaximumQuantityLineItem(LineItem item, int maximum) {
		super(item);
		this.maximum = maximum;
	}
	
	public int getMaximum() {
		return this.maximum;
	}
	
	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}
	
	@Override
	public boolean validates(Request req, Cart cart) {
		return super.validates(req, cart) && this.maximum >= this.getQuantity();
	}
	
	@Override
	public LineItem parse(Map<String, Object> context) {
		super.parse(context);
		this.maximum = Integer.parseInt((String) context.get("max"));
		return this;
	}
	
	public MaximumQuantityLineItem() {
		
	}
	
	public MaximumQuantityLineItem(LineItem item) {
		super(item);
	}

}
