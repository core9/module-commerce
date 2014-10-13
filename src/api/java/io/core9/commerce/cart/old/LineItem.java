package io.core9.commerce.cart.old;

import java.io.Serializable;


public class LineItem implements Serializable {

	private static final long serialVersionUID = -3744618595117264970L;
	
	private String id;
	private String producttype;
	private int quantity;
	private int price;
	private String description;
	private String image;
	private String link;

	
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
	
	public String getImage() {
		return image;
	}

	
	public void setImage(String image) {
		this.image = image;
	}

	public String getLink() {
		return link;
	}


	public void setLink(String link) {
		this.link = link;
	}
	
	public LineItem(String id, int quantity, int price, String description, String image, String link) {
		this.id = id;
		this.image = image;
		this.description = description;
		this.price = price;
		this.quantity = quantity;
		this.link = link;
	}

}
