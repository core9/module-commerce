package io.core9.commerce.cart;

interface LineItem {

	String getId();

	void setId(String id);
	
	String getProducttype();

	void setProducttype(String producttype);

	String getProductid();

	void setProductid(String productid);

	int getQuantity();

	void setQuantity(int quantity);
	
	int getPrice();
	
	void setPrice(int price);

	String getDescription();

	void setDescription(String description);

}
