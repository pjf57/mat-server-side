package com.cs.fwk.api;

import java.util.Collection;
import java.util.List;

/**
 * Defines and Element in the model. For example, a Cheetah Block
 * 
 * @author pjf
 *
 */
public interface Element extends Item {

	/**
	 * @return the short name of the Element
	 */
	public String getShortName();

	/**
	 * @return type name of Element
	 */
	public String getType();	
	
	/**
	 * @return HW type (int value) of Element
	 */
	public int getHWType();
	
	/**
	 * @return a collection of the attributes the Element has
	 */
	public Collection<Attribute> getAttributes();
	
	/**
	 * @return the inputs that the element has
	 */
	public List<InputPort> getInputs();

	/**
	 * @return the outputs that the element has
	 */
	public List<OutputPort> getOutputs();
	
	/**
	 * @return the commands that the element has
	 */
	public List<Cmd> getCmds();

	/**
	 * Set the status of the element
	 * Update an internal flag to indicate if the status was changed
	 * 
	 * @param newStatus
	 */
	public void setStatus(Status newStatus);

	/**
	 * @return the element status as an object
	 */
	public Status getElementStatus();

	/**
	 * Determine if status has changed
	 * 
	 * reset - true if want to reset the changed flag
	 */
	public boolean hasStatusChanged(boolean reset);

	/**
	 * @return the status of the Element as a collection of attributes
	 * @throws Exception
	 */
	public Collection<Attribute> getStatusAttrs() throws Exception;

	/**
	 * Select an attribute by name
	 * 
	 * @param name of the attr
	 * @return the selected attribute
	 * @throws Exception if attribute not found
	 */
	public Attribute getAttribute(String name) throws Exception;
	
	/**
	 * Select an output by name
	 * 
	 * @param name of the output
	 * @return selected output
	 * @throws Exception if no such output
	 */
	public OutputPort getOutput(String name) throws Exception;
	
	/**
	 * Remove all connections to this element
	 */
	public void removeAllConnections();
}
