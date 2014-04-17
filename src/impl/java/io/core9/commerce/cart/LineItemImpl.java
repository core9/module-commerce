package io.core9.commerce.cart;

import java.util.UUID;

public class LineItemImpl implements LineItem {

	private String id;
	private String producttype;
	private String productid;
	private int quantity;
	private int price;
	private String description;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}
	
	@Override
	public String getProducttype() {
		return producttype;
	}

	@Override
	public void setProducttype(String producttype) {
		this.producttype = producttype;
	}

	
	@Override
	public String getProductid() {
		return productid;
	}

	@Override
	public void setProductid(String productid) {
		this.productid = productid;
	}


	@Override
	public int getQuantity() {
		return quantity;
	}

	@Override
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	@Override
	public int getPrice() {
		return price;
	}

	@Override
	public void setPrice(int price) {
		this.price = price;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}
	
	public LineItemImpl(String producttype, String productid, int quantity, int price, String description) {
		this.id = UUID.randomUUID().toString();
		this.producttype = producttype;
		this.description = description;
		this.price = price;
		this.quantity = quantity;
		this.productid = productid;
	}

}
