package io.core9.commerce.cart.lineitem;

import java.util.Map;

import io.core9.commerce.cart.Cart;
import io.core9.commerce.cart.CartException;
import io.core9.plugin.server.request.Request;

public class SingularLineItem extends StandardLineItem {
	
	private static final long serialVersionUID = -2558721729361376972L;

	@Override
	public boolean validates(Request req, Cart cart) {
		if(super.validates(req, cart)) {
			try {
				this.setQuantity(1);
			} catch (CartException e) {
				e.printStackTrace();
			}
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public LineItem parse(Map<String, Object> context) throws NumberFormatException, CartException {
		super.parse(context);
		this.setQuantity(1);
		return this;
	}
	
	public SingularLineItem() {
		
	}
	
	public SingularLineItem(LineItem item) throws CartException {
		super(item);
		this.setQuantity(1);
	}

}
