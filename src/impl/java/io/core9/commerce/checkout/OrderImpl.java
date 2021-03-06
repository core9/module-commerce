package io.core9.commerce.checkout;

import io.core9.commerce.cart.Cart;
import io.core9.commerce.cart.lineitem.StandardLineItem;
import io.core9.plugin.database.repository.AbstractCrudEntity;
import io.core9.plugin.database.repository.Collection;
import io.core9.plugin.database.repository.CrudEntity;
import io.core9.plugin.server.request.Request;

import java.util.Map;

@Collection("core.orders")
public class OrderImpl extends AbstractCrudEntity implements CrudEntity, Order {

	private static final long serialVersionUID = -7647420231452397432L;
	
	private String _id;
	private int paymentCounter;
	private String sessionId;
	private Address billing;
	private Address shipping;
	private String paymentmethod;
	private Map<String,Object> paymentData;
	private Cart cart;
	private StandardLineItem shippingCost;
	private boolean finalized;
	private String remark;
	private String status;
	private String message;
	private String timestamp;
	
	public String getId() {
		if(_id == null) {
			_id = super.getId();
		}
		return _id;
	}
	
	@Override
	public String getPaymentId() {
		return getId() + "-" + paymentCounter;
	}
	
	@Override
	public int getPaymentCounter() {
		return this.paymentCounter;
	}
	
	@Override
	public void setPaymentCounter(int paymentCounter) {
		this.paymentCounter = paymentCounter;
	}
	
	@Override
	public Order incrementPaymentCounter() {
		this.paymentCounter++;
		return this;
	}
	
	public String get_id() {
		return getId();
	}
	
	public void setId(String id) {
		this._id = id;
	}
	
	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String getStatus() {
		return status;
	}

	@Override
	public void setStatus(String status) {
		this.status = status;
	}

	@Override
	public Address getBilling() {
		return billing;
	}

	@Override
	public void setBilling(Address billing) {
		this.billing = billing;
	}

	@Override
	public Address getShipping() {
		return shipping;
	}

	@Override
	public void setShipping(Address shipping) {
		this.shipping = shipping;
	}

	@Override
	public String getPaymentmethod() {
		return paymentmethod;
	}

	@Override
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
	public StandardLineItem getShippingCost() {
		return shippingCost;
	}

	@Override
	public void setShippingCost(StandardLineItem shippingCost) {
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

	@Override
	public boolean validates(Request req) {
		if((billing == null || billing.validates(req)) && 
		   (shipping == null || shipping.validates(req))) {
			return cart.validates(req);
		}
		return false;
	}

	@Override
	public String getTimestamp() {
		return this.timestamp;
	}

	@Override
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String getSessionId() {
		return this.sessionId;
	}

	@Override
	public Order setSessionId(String sessionId) {
		this.sessionId = sessionId;
		return this;
	}

}
