package io.core9.commerce.cart.lineitem;

public class SingularLineItem extends StandardLineItem {

	private static final long serialVersionUID = -2558721729361376972L;

	public SingularLineItem(LineItem item) {
		super(item);
		this.setQuantity(1);
	}
	
	public SingularLineItem(String id, int price, String description, String image, String link) {
		super(id, 1, price, description, image, link);
	}
	
	@Override
	public boolean validates() {
		if(super.validates()) {
			this.setQuantity(1);
			return true;
		} else {
			return false;
		}
	}

}
