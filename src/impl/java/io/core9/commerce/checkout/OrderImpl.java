package io.core9.commerce.checkout;

import java.util.Map;

import io.core9.commerce.cart.Cart;
import io.core9.commerce.cart.LineItem;
import io.core9.plugin.database.repository.AbstractCrudEntity;
import io.core9.plugin.database.repository.Collection;
import io.core9.plugin.database.repository.CrudEntity;

@Collection("core.orders")
public class OrderImpl extends AbstractCrudEntity implements CrudEntity, Order {

	/**
	 * Address for an order
	 * TODO make dynamic
	 * @author mark
	 *
	 */
	public class Address {
		private String company;
		private String fname;

		public String getCompany() {
			return company;
		}

		public void setCompany(String company) {
			this.company = company;
		}

		public String getFname() {
			return fname;
		}

		public void setFname(String fname) {
			this.fname = fname;
		}

		public String getLname() {
			return lname;
		}

		public void setLname(String lname) {
			this.lname = lname;
		}

		public String getStreet() {
			return street;
		}

		public void setStreet(String street) {
			this.street = street;
		}

		public String getStreet2() {
			return street2;
		}

		public void setStreet2(String street2) {
			this.street2 = street2;
		}

		public String getPostalcode() {
			return postalcode;
		}

		public void setPostalcode(String postalcode) {
			this.postalcode = postalcode;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}

		public String getTelephone() {
			return telephone;
		}

		public void setTelephone(String telephone) {
			this.telephone = telephone;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		private String lname;
		private String street;
		private String street2;
		private String postalcode;
		private String city;
		private String country;
		private String telephone;
		private String email;
	}

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
