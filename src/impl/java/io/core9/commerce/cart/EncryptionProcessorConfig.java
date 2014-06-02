package io.core9.commerce.cart;

import io.core9.plugin.importer.processor.ImporterConfig;

public class EncryptionProcessorConfig extends ImporterConfig {

	private String contentType;

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
}
