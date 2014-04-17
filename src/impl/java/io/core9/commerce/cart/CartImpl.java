package io.core9.commerce.cart;

import java.util.HashMap;
import java.util.Map;

public class CartImpl implements Cart {
	
	Map<String, LineItem> items = new HashMap<String, LineItem>();

	@Override
	public void addItem(String id, int quantity, int price, String description) {
		addItem(new LineItemImpl("product", id, quantity, price, description));
	}

	@Override
	public Map<String, LineItem> getItems() {
		return this.items;
	}

	@Override
	public void addItem(LineItem item) {
		items.put(item.getId(), item);
	}

}
