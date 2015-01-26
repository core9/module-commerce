package io.core9.commerce.cart.lineitem;

import io.core9.commerce.cart.CartException;

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
	public LineItem create(Map<String, Object> context) throws CartException {
		String type = (String) context.get("type");
		if(type != null && TYPES.containsKey(type)) {
			try {
				LineItem item = TYPES.get(type).newInstance();
				return item.parse(context);
			} catch (IllegalAccessException | InstantiationException e) {
				// Just parses as a StandardLineItem
			}
		}
		try {
			return TYPES.get("standard").newInstance().parse(context);
		} catch (IllegalAccessException | InstantiationException e) {
			return new StandardLineItem().parse(context);
		}
	}

	@Override
	public void execute() {
		TYPES.put("max", MaximumQuantityLineItem.class);
		TYPES.put("single", SingularLineItem.class);
		TYPES.put("standard", StandardLineItem.class);
	}
}
