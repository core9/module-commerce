package io.core9.commerce.cart.lineitem;


public class StandardLineItem implements LineItem {

	private static final long serialVersionUID = -3744618595117264970L;
	private static final String LINE_ITEM_TYPE = "standard";

	private String id;
	private String producttype;
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
	public String getProducttype() {
		return producttype;
	}

	@Override
	public void setProducttype(String producttype) {
		this.producttype = producttype;
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

	public StandardLineItem() {

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
	public boolean validates() {
		return true;
	}

	public StandardLineItem(String id, int quantity, int price, String description, String image, String link) {
		this.id = id;
		this.image = image;
		this.description = description;
		this.price = price;
		this.quantity = quantity;
		this.link = link;
	}

	public StandardLineItem(LineItem item) {
		this.id = item.getId();
		this.image = item.getImage();
		this.description = item.getDescription();
		this.price = item.getPrice();
		this.quantity = item.getQuantity();
		this.link = item.getLink();
		this.producttype = item.getProducttype();
	}

	@Override
	public String getType() {
		return LINE_ITEM_TYPE;
	}

}
