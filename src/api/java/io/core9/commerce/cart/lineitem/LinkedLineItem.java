package io.core9.commerce.cart.lineitem;

import java.util.List;

public class LinkedLineItem extends StandardLineItem implements LineItem {

	private static final long serialVersionUID = -4391511070861579303L;
	
	private List<String> linkedLineItems;

	public List<String> getLinkedLineItems() {
		return linkedLineItems;
	}

	public void setLinkedLineItems(List<String> linkedLineItems) {
		this.linkedLineItems = linkedLineItems;
	}
	
	public LinkedLineItem(LineItem item, List<String> linkedLineItems) {
		super(item);
		this.linkedLineItems = linkedLineItems;
	}

}
