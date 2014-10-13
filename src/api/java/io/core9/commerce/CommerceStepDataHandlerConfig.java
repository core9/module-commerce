package io.core9.commerce;

import io.core9.plugin.widgets.Component;
import io.core9.plugin.widgets.datahandler.DataHandlerDefaultConfig;

import java.util.List;

public class CommerceStepDataHandlerConfig extends DataHandlerDefaultConfig {

	private List<Component> components;
	private String nextStep;
		
	/**
	 * @return the components
	 */
	public List<Component> getComponents() {
		return components;
	}

	/**
	 * @param components the components to set
	 */
	public void setComponents(List<Component> components) {
		this.components = components;
	}

	public String getNextStep() {
		return nextStep;
	}

	public void setNextStep(String nextStep) {
		this.nextStep = nextStep;
	}
}
