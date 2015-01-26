package io.core9.commerce.cart.lineitem;

import java.util.Map;

import io.core9.commerce.cart.Cart;
import io.core9.commerce.cart.CartException;
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
	public void setQuantity(int quantity) throws CartException {
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

	public StandardLineItem(LineItem item) throws CartException {
		this.setId(item.getId());
		this.setImage(item.getImage());
		this.setDescription(item.getDescription());
		this.setPrice(item.getPrice());
		this.setQuantity(item.getQuantity());
		this.setLink(item.getLink());
	}

	@Override
	public LineItem parse(Map<String, Object> context) throws NumberFormatException, CartException {
		if(context.get("itemid") != null) {
			this.setId((String) context.get("itemid"));
		}
		if(context.get("price") != null) {
			this.setPrice(Integer.parseInt((String) context.get("price")));
		}
		if(context.get("description") != null) {
			this.setDescription((String) context.get("description"));
		}
		if(context.get("image") != null) {
			this.setImage((String) context.get("image"));
		}
		if(context.get("link") != null) {
			this.setLink((String) context.get("link"));
		}
		if(context.get("quantity") != null) {
			this.setQuantity(Integer.parseInt((String) context.get("quantity")));
		}
		return this;
	}

	@Override
	public void delete() {
		
	}

}
