package io.core9.commerce.cart;

import io.core9.commerce.cart.lineitem.LineItem;
import io.core9.commerce.cart.lineitem.StandardLineItem;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class Cart implements Serializable {
	
	private static final long serialVersionUID = 2485275694155939983L;
	
	Map<String, LineItem> items = new HashMap<String, LineItem>();

	public void addItem(String id, int quantity, int price, String description, String image, String link) {
		addItem(new StandardLineItem(id, quantity, price, description, image, link));
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
	
	@JsonIgnore
	public int getTotal() {
		int total = 0;
		for(LineItem item : items.values()) {
			total += (item.getPrice() * item.getQuantity());
		}
		return total;
	}

	public boolean validates() {
		for(LineItem item : items.values()) {
			if(!item.validates()) {
				return false;
			}
		}
		return true;
	}

}
