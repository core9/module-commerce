package io.core9.commerce;

import io.core9.core.plugin.Core9Plugin;
import io.core9.plugin.widgets.datahandler.DataHandlerFactory;

/**
 * TODO This interface could be transformed to a more general FormDataHandler
 * @author mark
 *
 */
public interface CommerceDataHandlerFactory<T extends CommerceDataHandlerConfig> extends DataHandlerFactory<T>, Core9Plugin {
	
}
