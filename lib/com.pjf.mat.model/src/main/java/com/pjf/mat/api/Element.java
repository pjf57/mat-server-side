package com.pjf.mat.api;

import java.util.Collection;
import java.util.List;

/** @model */
public interface Element extends Item {
	/** @model */
	public String getType();
	/** @model */
	public int getHWType();
	/** @model transient="true" volatile="true" changeable="false" */
	public Collection<Attribute> getAttributes();
	/** @throws Exception 
	 * @model transient="true" volatile="true" changeable="false" */
	public Collection<Attribute> getStatusAttrs() throws Exception;
	/** @model type="InputPort" containment="true" */
	public List<InputPort> getInputs();
	/** @model type="OutputPort" containment="true" */
	public List<OutputPort> getOutputs();
	/** @model type="Cmd" containment="true" */
	public List<Cmd> getCmds();
	/** @model transient="true" volatile="true" changeable="false" */
	public Status getElementStatus();
	
	public Attribute getAttribute(String name);	// return null if not found
	public void setStatus(Status newStatus);
	
	public String getShortName();

	/**
	 * @return output by name
	 * @throws Exception if no such output
	 */
	public OutputPort getOutput(String name) throws Exception;
}
