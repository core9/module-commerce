package io.core9.commerce.coupon;

import io.core9.commerce.cart.Cart;

public interface CouponHandler {

	Coupon handle(Coupon coupon, Cart cart);

}

