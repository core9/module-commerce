package io.core9.commerce.cart.lineitem;

import java.util.Map;

import io.core9.commerce.cart.Cart;
import io.core9.plugin.server.request.Request;

public class CustomizableLineItem extends StandardLineItem {
	
	public static final String LINE_ITEM_TYPE = "customizable";

	private Map<String,Object> extras;
	
	private static final long serialVersionUID = -709757267743552352L;

	public CustomizableLineItem(LineItem item, Map<String,Object> extras) {
		super(item);
		this.extras = extras;
	}
	
	public CustomizableLineItem(String id, int quantity, int price, String description, String image, String link, Map<String,Object> extras) {
		super(id, quantity, price, description, image, link);
		this.extras = extras;
	}
	
	public Map<String,Object> getExtras() {
		return this.extras;
	}
	
	@Override
	public boolean validates(Request req, Cart cart) {
		return super.validates(req, cart);
	}
}
