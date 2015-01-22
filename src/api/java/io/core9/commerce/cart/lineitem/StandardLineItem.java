package io.core9.commerce.cart.lineitem;

import java.util.Map;

import io.core9.commerce.cart.Cart;
import io.core9.plugin.server.request.Request;

public class StandardLineItem implements LineItem {

	private static final long serialVersionUID = -3744618595117264970L;

	private String id;
	private int quantity;
	private int price;
	private String description;
	private String image;
	private String link;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
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

	@Override
	public String getImage() {
		return image;
	}

	@Override
	public void setImage(String image) {
		this.image = image;
	}

	@Override
	public String getLink() {
		return link;
	}

	@Override
	public void setLink(String link) {
		this.link = link;
	}
	
	@Override
	public boolean validates(Request req, Cart cart) {
		return true;
	}
	
	public StandardLineItem() {

	}

	public StandardLineItem(LineItem item) {
		this.id = item.getId();
		this.image = item.getImage();
		this.description = item.getDescription();
		this.price = item.getPrice();
		this.quantity = item.getQuantity();
		this.link = item.getLink();
	}

	@Override
	public LineItem parse(Map<String, Object> context) {
		this.id  = (String) context.get("itemid");
		this.price = Integer.parseInt((String) context.get("price"));
		this.description = (String) context.get("description"); 
		this.image = (String) context.get("image");
		this.link = (String) context.get("link");
		return this;
	}

}
