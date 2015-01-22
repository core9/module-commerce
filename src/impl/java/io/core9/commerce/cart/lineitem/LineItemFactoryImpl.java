package io.core9.commerce.cart.lineitem;

import java.util.HashMap;
import java.util.Map;

import net.xeoh.plugins.base.annotations.PluginImplementation;

@PluginImplementation
public class LineItemFactoryImpl implements LineItemFactory {
	
	private final Map<String,Class<? extends LineItem>> TYPES = new HashMap<String, Class<? extends LineItem>>();

	@Override
	public void addLineItemType(String type, Class<? extends LineItem> clazz) {
		TYPES.put(type, clazz);
	}

	@Override
	public LineItem create(Map<String, Object> context) {
		String type = (String) context.get("type");
		if(type != null && TYPES.containsKey(type)) {
			try {
				LineItem item = TYPES.get(type).newInstance();
				return item.parse(context);
			} catch (IllegalAccessException | InstantiationException e) {
				// Just parses as a StandardLineItem
			}
		}
		return new StandardLineItem().parse(context);
	}
}
