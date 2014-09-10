package io.core9.commerce.checkout;

import io.core9.plugin.widgets.datahandler.DataHandlerDefaultConfig;
import io.core9.plugin.widgets.datahandler.DataHandlerFactoryConfig;

import java.util.List;

public class CheckoutDataHandlerConfig extends DataHandlerDefaultConfig	implements DataHandlerFactoryConfig {
	
	private List<String> processors;
	private String nextStep;
	private boolean destroySession;
	
	private String mailerProfile;
	private String mailerFromAddress;
	private String mailerSubject;
	private String mailerTemplate;

	public String getNextStep() {
		return nextStep;
	}

	public void setNextStep(String nextStep) {
		this.nextStep = nextStep;
	}

	public boolean getDestroySession() {
		return destroySession;
	}

	public void setDestroySession(boolean destroySession) {
		this.destroySession = destroySession;
	}

	public List<String> getProcessors() {
		return processors;
	}

	public void setProcessors(List<String> processors) {
		this.processors = processors;
	}
	
	public String getMailerProfile() {
		return this.mailerProfile;
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
