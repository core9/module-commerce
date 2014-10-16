package io.core9.commerce.checkout;

import io.core9.plugin.widgets.datahandler.DataHandlerDefaultConfig;

public class OrderFinalizerDataHandlerConfig extends DataHandlerDefaultConfig {

	private String mailerProfile;
	private String mailerFromAddress;
	private String mailerSubject;
	private String mailerTemplate;

	public String getMailerProfile() {
		return mailerProfile;
	}

	public void setMailerProfile(String mailerProfile) {
		this.mailerProfile = mailerProfile;
	}

	public String getMailerFromAddress() {
		return mailerFromAddress;
	}

	public void setMailerFromAddress(String mailerFromAddress) {
		this.mailerFromAddress = mailerFromAddress;
	}

	public String getMailerSubject() {
		return mailerSubject;
	}

	public void setMailerSubject(String mailerSubject) {
		this.mailerSubject = mailerSubject;
	}

	public String getMailerTemplate() {
		return mailerTemplate;
	}

	public void setMailerTemplate(String mailerTemplate) {
		this.mailerTemplate = mailerTemplate;
	}

}
