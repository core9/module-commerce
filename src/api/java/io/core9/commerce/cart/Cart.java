package io.core9.commerce.cart;

import io.core9.commerce.cart.lineitem.LineItem;
import io.core9.commerce.cart.lineitem.StandardLineItem;
import io.core9.plugin.server.request.Request;
import io.core9.plugin.server.request.RequestUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class Cart implements Serializable {
	
	private static final long serialVersionUID = 2485275694155939983L;
	
	@JsonDeserialize(contentAs=StandardLineItem.class)
	Map<String, LineItem> items = new HashMap<String, LineItem>();

	public Map<String, LineItem> getItems() {
		return this.items;
	}

	public void addItem(Request req, LineItem item) {
		if(items.containsKey(item.getId())) {
			try {
				item.setQuantity(item.getQuantity() + items.get(item.getId()).getQuantity());
			} catch (CartException e) {
				RequestUtils.addMessage(req, e.getMessage(), e.getArgs());
			}
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

	public boolean validates(Request req) {
		for(LineItem item : items.values()) {
			if(!item.validates(req, this)) {
				return false;
			}
		}
		return true;
	}

}
