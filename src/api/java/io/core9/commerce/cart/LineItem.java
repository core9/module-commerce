package io.core9.commerce.cart;

import java.util.UUID;

public class LineItem {

	private String id;
	private String producttype;
	private String productid;
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

	
	
	public String getProductid() {
		return productid;
	}

	
	public void setProductid(String productid) {
		this.productid = productid;
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
	
	public LineItem(String producttype, String productid, int quantity, int price, String description) {
		this.id = UUID.randomUUID().toString();
		this.producttype = producttype;
		this.description = description;
		this.price = price;
		this.quantity = quantity;
		this.productid = productid;
	}

}
