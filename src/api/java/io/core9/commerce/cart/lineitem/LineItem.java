package io.core9.commerce.cart.lineitem;

import java.io.Serializable;

public interface LineItem extends Serializable {

	String getId(); 

	void setId(String id);

	String getProducttype();

	void setProducttype(String producttype);

	int getQuantity();

	void setQuantity(int quantity);

	int getPrice();

	void setPrice(int price);

	String getDescription();

	void setDescription(String description);

	String getImage();

	void setImage(String image);

	String getLink();

	void setLink(String link);

	boolean validates();
}
