package io.core9.commerce.cart.lineitem;

import io.core9.commerce.cart.Cart;
import io.core9.plugin.server.request.Request;

public class CouponLineItem extends SingularLineItem {
	
	private static final long serialVersionUID = 7271873568037827041L;
	
	public CouponLineItem(LineItem item) {
		super(item);
	}
	
	public CouponLineItem(String id, int price, String description, String image, String link) {
		super(id, price, description, image, link);
	}
	
	@Override
	public boolean validates(Request req, Cart cart) {
		return true;
	}

}
