package io.core9.commerce.checkout;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.core9.commerce.cart.Cart;
import io.core9.commerce.cart.LineItem;
import io.core9.plugin.database.repository.AbstractCrudEntity;
import io.core9.plugin.database.repository.Collection;
import io.core9.plugin.database.repository.CrudEntity;

@Collection("core.orders")
public class OrderImpl extends AbstractCrudEntity implements CrudEntity, Order {

	private Address billing;
	private Address shipping;
	private String paymentmethod;
	private Map<String,Object> paymentData;
	private Cart cart;
	private LineItem shippingCost;
	private boolean finalized;
	private String remark;

	public Address getBilling() {
		return billing;
	}

	public void setBilling(Address billing) {
		this.billing = billing;
	}

	public Address getShipping() {
		return shipping;
	}

	public void setShipping(Address shipping) {
		this.shipping = shipping;
	}

	public String getPaymentmethod() {
		return paymentmethod;
	}

	public void setPaymentmethod(String paymentmethod) {
		this.paymentmethod = paymentmethod;
	}

	@Override
	public Cart getCart() {
		return cart;
	}

	@Override
	public void setCart(Cart cart) {
		this.cart = cart;
	}

	@Override
	public LineItem getShippingCost() {
		return shippingCost;
	}

	@Override
	public void setShippingCost(LineItem shippingCost) {
		this.shippingCost = shippingCost;
	}

	@Override
	public Map<String, Object> getPaymentData() {
		return paymentData;
	}

	@Override
	public void setPaymentData(Map<String, Object> data) {
		this.paymentData = data;
	}

	@Override
	public boolean isFinalized() {
		return this.finalized;
	}

	@Override
	public void setFinalized(boolean finalized) {
		this.finalized = finalized;
	}

	@JsonIgnore
	@Override
	public int getTotal() {
		if(shippingCost != null) {
			return cart.getTotal()  + shippingCost.getPrice();
		}
		return cart.getTotal();
	}

	@Override
	public String getRemark() {
		return remark;
	}

	@Override
	public void setRemark(String remark) {
		this.remark = remark;
	}

}
