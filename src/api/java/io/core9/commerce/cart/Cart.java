package io.core9.commerce.cart;

import java.util.Map;

public interface Cart {
	
	Map<String, LineItem> getItems();
	
	void addItem(LineItem item);
	
	void addItem(String id, int quantity, int price, String description);

}
