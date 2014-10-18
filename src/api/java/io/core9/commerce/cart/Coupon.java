package io.core9.commerce.cart;

import java.util.List;

import io.core9.plugin.database.repository.AbstractCrudEntity;
import io.core9.plugin.database.repository.Collection;
import io.core9.plugin.database.repository.CrudEntity;

@Collection("core.coupons")
public class Coupon extends AbstractCrudEntity implements CrudEntity {

	private List<String> applicableSkus;
	private byte percentage;
	private int amount;
	private int redemptions;
	private boolean active;

	public List<String> getApplicableSkus() {
		return applicableSkus;
	}

	public void setApplicableSkus(List<String> applicableSkus) {
		this.applicableSkus = applicableSkus;
	}

	public byte getPercentage() {
		return percentage;
	}

	public void setPercentage(byte percentage) {
		this.percentage = percentage;
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public int getRedemptions() {
		return redemptions;
	}

	public void setRedemptions(int redemptions) {
		this.redemptions = redemptions;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void decrement() {
		this.redemptions--;
		if(this.redemptions < 1) {
			this.active = false;
		}
	}
	
	public void increment() {
		this.redemptions++;
		if(this.redemptions > 0) {
			this.active = true;
		}
	}

}
