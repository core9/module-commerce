package io.core9.commerce.cart.lineitem;

import java.util.Map;

import io.core9.commerce.cart.Cart;
import io.core9.commerce.cart.CartException;
import io.core9.plugin.server.request.Request;

public class CustomizableLineItem extends StandardLineItem {
	
	private static final long serialVersionUID = -709757267743552352L;
	
	private Map<String,Object> extras;
	
	public CustomizableLineItem(LineItem item) throws CartException {
		super(item);
	}
	
	public CustomizableLineItem() {
		
	}

	public Map<String,Object> getExtras() {
		return this.extras;
	}
	
	@Override
	public boolean validates(Request req, Cart cart) {
		return super.validates(req, cart);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public LineItem parse(Map<String, Object> context) throws NumberFormatException, CartException {
		super.parse(context);
		this.extras = (Map<String, Object>) context.get("extras");
		return this;
	}
}
