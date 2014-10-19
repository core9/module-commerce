package io.core9.commerce.cart.lineitem;

public class CouponLineItem extends SingularLineItem {
	
	private static final String LINE_ITEM_TYPE = "coupon";

	private static final long serialVersionUID = 7271873568037827041L;
	
	@Override
	public String getType() {
		return LINE_ITEM_TYPE;
	}

	public CouponLineItem(LineItem item) {
		super(item);
	}
	
	public CouponLineItem(String id, int price, String description, String image, String link) {
		super(id, price, description, image, link);
	}

}
