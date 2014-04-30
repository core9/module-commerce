package io.core9.commerce.cart;


public class LineItem {

	private String id;
	private String producttype;
	private int quantity;
	private int price;
	private String description;

	
	public String getId() {
		return id;
	}

	
	public void setId(String id) {
		this.id = id;
	}
	
	
	public String getProducttype() {
		return producttype;
	}

	
	public void setProducttype(String producttype) {
		this.producttype = producttype;
	}

	public int getQuantity() {
		return quantity;
	}

	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	
	public int getPrice() {
		return price;
	}

	
	public void setPrice(int price) {
		this.price = price;
	}

	
	public String getDescription() {
		return description;
	}

	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public LineItem() {
		
	}
	
	public LineItem(String id, int quantity, int price, String description) {
		this.id = id;
		this.description = description;
		this.price = price;
		this.quantity = quantity;
	}

}
