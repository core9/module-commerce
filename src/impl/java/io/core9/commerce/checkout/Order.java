package io.core9.commerce.checkout;

import io.core9.plugin.database.repository.AbstractCrudEntity;
import io.core9.plugin.database.repository.Collection;
import io.core9.plugin.database.repository.CrudEntity;

@Collection("core.orders")
public class Order extends AbstractCrudEntity implements CrudEntity {

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

		public String getTelephone() {
			return telephone;
		}

		public void setTelephone(String telephone) {
			this.telephone = telephone;
		}

		private String lname;
		private String street;
		private String street2;
		private String postalcode;
		private String city;
		private String telephone;
	}

	private Address billing;
	private Address shipping;
	private String paymentmethod;

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

}
