package com.cs.fwk.api;

import java.util.Collection;
import java.util.List;

import com.cs.fwk.api.gridattr.GridAttribute;
import com.cs.fwk.api.util.CBConfigText;

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
	 * @return the input with the specified name
	 * @throws Exception if no such input
	 */
	public InputPort getInput(String name) throws Exception;

	/**
	 * @return the outputs that the element has
	 */
	public List<OutputPort> getOutputs();

	/**
	 * @return the commands that the element has
	 */
	public List<Cmd> getCmds();
	
	/**
	 * @return list of string that describe current config. May return null.
	 * @throws Exception if an error occurred
	 */
	public CBConfigText getConfigText() throws Exception;

	/**
	 * Set the name of the class that can calculate the config textual description
	 * based on the current attribute values
	 * 
	 * @param className - name of class that implements ConfigTextCalcInt
	 */
	public void setConfigTextCalc(String className);

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
	 * Select a grid type attribute by name
	 * 
	 * @param name of the attr
	 * @return the selected attribute
	 * @throws Exception if attribute not found
	 */
	public GridAttribute getGridAttribute(String name) throws Exception;

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
	
	/**
	 * @return true if the cb has at least one calculated attribute
	 */
	public boolean hasCalculatedAttrs();

	/**
	 * @return name of class that can calculate a textual description of the config, or null
	 */
	public String getConfigInterpreterClassName();
}
