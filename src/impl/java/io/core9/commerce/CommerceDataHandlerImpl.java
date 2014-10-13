package io.core9.commerce;

import io.core9.plugin.server.request.Request;
import io.core9.plugin.widgets.Component;
import io.core9.plugin.widgets.datahandler.ContextualDataHandler;
import io.core9.plugin.widgets.datahandler.DataHandler;
import io.core9.plugin.widgets.datahandler.DataHandlerFactoryConfig;
import io.core9.plugin.widgets.widget.Widget;
import io.core9.plugin.widgets.widget.WidgetFactory;

import java.util.HashMap;
import java.util.Map;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

@PluginImplementation
public class CommerceDataHandlerImpl implements CommerceDataHandlerFactory {
	
	public static final String CONTEXT_SEPARATOR   = ".";
	
	@InjectPlugin
	private WidgetFactory widgets;

	@Override
	public String getName() {
		return "Commerce-Bundle";
	}

	@Override
	public Class<? extends DataHandlerFactoryConfig> getConfigClass() {
		return CommerceDataHandlerConfig.class;
	}

	@Override
	public DataHandler<CommerceDataHandlerConfig> createDataHandler(DataHandlerFactoryConfig options) {
		final CommerceDataHandlerConfig config = (CommerceDataHandlerConfig) options;
		return new DataHandler<CommerceDataHandlerConfig>() {
			
			@Override
			public Map<String, Object> handle(Request req) {
				Map<String, Object> result = new HashMap<String, Object>();
				if(config.getComponents() != null){
					Map<String,Object> form = parseRequestBody(req);
					for(Component component : config.getComponents()) {
						result.put(component.getName(), handleComponent(config, component, req, form));
					}
				}
				return result;
			}

			@Override
			public CommerceDataHandlerConfig getOptions() {
				return config;
			}
		};
	}

	@SuppressWarnings("unchecked")
	protected Map<String,Object> handleComponent(CommerceDataHandlerConfig config, Component component, Request req, Map<String, Object> form) {
		Widget widget = widgets.getRegistry(req.getVirtualHost()).get(component.getId());
		DataHandler<?> handler;
		if((handler = widget.getDataHandler()) != null) {
			handler.getOptions().setComponentName(component.getName());
			if(component.getGlobals().size() > 0) {
				putGlobalValuesOnContext(config, component, req);
			}
			if(handler instanceof ContextualDataHandler) {
				return ((ContextualDataHandler<CommerceDataHandlerConfig>) handler).handle(req, (Map<String, Object>) form.get(component.getName()));
			} else {
				return handler.handle(req);
			}
		} else {
			return new HashMap<String,Object>();
		}
	}

	protected void putGlobalValuesOnContext(CommerceDataHandlerConfig config, Component component, Request req) {
		for(Map.Entry<String,String> entry : component.getGlobals().entrySet()) {
			if(entry.getValue().startsWith(":")) {
				req.putContext(component.getName() + CONTEXT_SEPARATOR + entry.getKey(), req.getContext(config.getComponentName() + CONTEXT_SEPARATOR + entry.getValue().substring(1)));
			} else {
				req.putContext(component.getName() + CONTEXT_SEPARATOR + entry.getKey(), entry.getValue());
			}
		}
	}
	
	protected Map<String, Object> parseRequestBody(Request req) {
		Map<String,Object> form = new HashMap<String, Object>();
		switch (req.getMethod()) {
		case POST:
		case PUT:
		case DELETE:
			for(Map.Entry<String,Object> entry : req.getBodyAsMap().toBlocking().last().entrySet()) {
				parseField(form, entry.getKey(), entry.getValue());
			}
		default:
			return form;
		}
	}
	
	/**
	 * Parse a form request to a map
	 * @param result
	 * @param fieldname
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	private void parseField(Map<String,Object> result, String fieldname, Object value) {
		int start = fieldname.indexOf('[');
		int end = fieldname.indexOf(']');
		if(start != -1 && end != -1) {
			String field = fieldname.substring(0, start);
			if(!result.containsKey(field)) {
				result.put(field, new HashMap<String,Object>());
			}
			String innerfield = fieldname.substring(start + 1, end) + fieldname.substring(end + 1);
			parseField((Map<String, Object>) result.get(field), innerfield, value);
		} else {
			result.put(fieldname, value);
		}
	}
}
