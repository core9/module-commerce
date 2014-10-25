package io.core9.commerce.checkout;

import io.core9.plugin.server.request.Request;
import io.core9.plugin.server.request.RequestUtils;

import java.io.Serializable;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class Address implements Serializable {
	
	private static final long serialVersionUID = 2659845489254965523L;
	private String company;
	private String fname;
	private String lname;
	private String street;
	private String street2;
	private String postalcode;
	private String city;
	private String country;
	private String telephone;
	private String email;

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
	
	public boolean validates(Request req) {
		boolean result = true;
		if(this.fname == null || this.fname.equals("")) {
			RequestUtils.addMessage(req, "First name cannot be empty.");
			result = false;
		}
		if(this.lname == null || this.lname.equals("")) {
			RequestUtils.addMessage(req, "Last name cannot be empty.");
			result = false;
		}
		if(this.street == null || this.street.equals("")) {
			RequestUtils.addMessage(req, "Street name cannot be empty.");
			result = false;
		}
		if(this.street2 == null || this.street2.equals("")) {
			RequestUtils.addMessage(req, "Street2 cannot be empty.");
			result = false;
		}
		if(this.postalcode == null || this.postalcode.equals("")) {
			RequestUtils.addMessage(req, "Postal code cannot be empty.");
			result = false;
		}
		if(this.city == null || this.city.equals("")) {
			RequestUtils.addMessage(req, "City cannot be empty.");
			result = false;
		}
		if(this.country == null || this.country.equals("")) {
			RequestUtils.addMessage(req, "Country cannot be empty.");
			result = false;
		}
		if(this.email == null || this.email.equals("")) {
			RequestUtils.addMessage(req, "Email cannot be empty.");
			result = false;
		}
		try {
			InternetAddress emailAddr = new InternetAddress(email);
			emailAddr.validate();
		} catch (AddressException e) {
			RequestUtils.addMessage(req, "Email is not in the normal format (user@domain.tld).");
			result = false;
		}
		return result;
	}
}
