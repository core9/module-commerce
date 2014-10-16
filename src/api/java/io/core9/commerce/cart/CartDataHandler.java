package io.core9.commerce.cart;

import io.core9.core.plugin.Core9Plugin;
import io.core9.plugin.widgets.datahandler.DataHandlerDefaultConfig;
import io.core9.plugin.widgets.datahandler.DataHandlerFactory;

public interface CartDataHandler<T extends DataHandlerDefaultConfig> extends DataHandlerFactory<T>, Core9Plugin {

}
