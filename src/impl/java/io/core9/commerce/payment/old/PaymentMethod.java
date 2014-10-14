package io.core9.commerce.payment.old;

import java.util.HashMap;
import java.util.Map;

import io.core9.plugin.database.repository.AbstractCrudEntity;
import io.core9.plugin.database.repository.Collection;
import io.core9.plugin.database.repository.CrudEntity;

@Collection("configuration")
public class PaymentMethod extends AbstractCrudEntity implements CrudEntity {
	
	public static final Map<String,Object> DEFAULT_QUERY = new HashMap<String, Object>();
	
	static {
		DEFAULT_QUERY.put("configtype", "paymentmethod");
	}

	private String configtype;
	private String name;
	private String label;
	private String widget;
	
	public String getConfigtype() {
		if(configtype != null) {
			return configtype;
		}
		return "paymentmethod";
	}
	
	public void setConfigtype(String configtype) {
		this.configtype = configtype;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getWidget() {
		return widget;
	}

	public void setWidget(String widget) {
		this.widget = widget;
	}
	
	@Override
	public Map<String,Object> retrieveDefaultQuery() {
		return DEFAULT_QUERY;
	}

}
