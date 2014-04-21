package io.core9.commerce.cart;

import java.util.HashMap;
import java.util.Map;

public class Cart {
	
	Map<String, LineItem> items = new HashMap<String, LineItem>();

	public void addItem(String id, int quantity, int price, String description) {
		addItem(new LineItem("product", id, quantity, price, description));
	}

	public Map<String, LineItem> getItems() {
		return this.items;
	}

	public void addItem(LineItem item) {
		items.put(item.getId(), item);
	}

}
