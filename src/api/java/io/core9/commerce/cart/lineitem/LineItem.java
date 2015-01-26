package io.core9.commerce.cart.lineitem;

import io.core9.commerce.cart.Cart;
import io.core9.commerce.cart.CartException;
import io.core9.plugin.server.request.Request;

import java.io.Serializable;
import java.util.Map;

public interface LineItem extends Serializable {

	String getId(); 

	void setId(String id);

	int getQuantity();

	void setQuantity(int quantity) throws CartException;

	int getPrice();

	void setPrice(int price);

	String getDescription();

	void setDescription(String description);

	String getImage();

	void setImage(String image);

	String getLink();

	void setLink(String link);

	boolean validates(Request req, Cart cart);

	LineItem parse(Map<String, Object> context) throws CartException;

	void delete();
}
