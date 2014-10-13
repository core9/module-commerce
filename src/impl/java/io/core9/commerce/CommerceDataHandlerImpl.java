package io.core9.commerce;

import io.core9.plugin.server.request.Request;
import io.core9.plugin.widgets.Component;
import io.core9.plugin.widgets.datahandler.DataHandler;
import io.core9.plugin.widgets.datahandler.DataHandlerFactoryConfig;
import io.core9.plugin.widgets.widget.Widget;
import io.core9.plugin.widgets.widget.WidgetFactory;

import java.util.HashMap;
import java.util.Map;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.annotations.injections.InjectPlugin;

@PluginImplementation
public class CommerceDataHandlerImpl implements CommerceDataHandler {
	
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
				if(config.getComponents() == null){
					return result;
				}
				for(Component component : config.getComponents()) {
					if(component.getGlobals().size() > 0) {
						for(Map.Entry<String,String> entry : component.getGlobals().entrySet()) {
							if(entry.getValue().startsWith(":")) {
								req.putContext(component.getName() + "." + entry.getKey(), req.getContext(options.getComponentName() + "." + entry.getValue().substring(1)));
							} else {
								req.putContext(component.getName() + "." + entry.getKey(), entry.getValue());
							}
						}
					}
					Widget widget = widgets.getRegistry(req.getVirtualHost()).get(component.getId());
					DataHandler<?> handler;
					if((handler = widget.getDataHandler()) != null) {
						handler.getOptions().setComponentName(component.getName());
						result.put(component.getName(), handler.handle(req));
					} else {
						result.put(component.getName(), new HashMap<String,Object>());
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

}
