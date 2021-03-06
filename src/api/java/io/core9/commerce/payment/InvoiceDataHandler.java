package io.core9.commerce.payment;

import io.core9.core.plugin.Core9Plugin;
import io.core9.plugin.widgets.datahandler.DataHandlerDefaultConfig;
import io.core9.plugin.widgets.datahandler.DataHandlerFactory;

public interface InvoiceDataHandler<T extends DataHandlerDefaultConfig> extends DataHandlerFactory<T>, Core9Plugin {

}
