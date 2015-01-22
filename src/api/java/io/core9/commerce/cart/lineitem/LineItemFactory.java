package io.core9.commerce.cart.lineitem;

import java.util.Map;

import io.core9.core.plugin.Core9Plugin;

public interface LineItemFactory extends Core9Plugin {

	public void addLineItemType(String type, Class<? extends LineItem> clazz);
	
	public LineItem create(Map<String,Object> context);
	
}
