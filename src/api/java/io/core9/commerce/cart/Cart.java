package io.core9.commerce.cart;

import java.util.HashMap;
import java.util.Map;

public class Cart {
	
	Map<String, LineItem> items = new HashMap<String, LineItem>();

	public void addItem(String id, int quantity, int price, String description) {
		addItem(new LineItem(id, quantity, price, description));
	}

	public Map<String, LineItem> getItems() {
		return this.items;
	}

	public void addItem(LineItem item) {
		if(items.containsKey(item.getId())) {
			item.setQuantity(item.getQuantity() + items.get(item.getId()).getQuantity());
		}
		items.put(item.getId(), item);
	}
	
	public int getTotal() {
		int total = 0;
		for(LineItem item : items.values()) {
			total += (item.getPrice() * item.getQuantity());
		}
		return total;
	}

}
