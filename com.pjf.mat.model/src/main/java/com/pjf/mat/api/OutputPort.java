package com.pjf.mat.api;

/** @model */
public interface OutputPort extends Cloneable {
	/** @model */
	public int getId();
	/** @model opposite="outputs"*/
	public Element getParent();
	/** @model */
	public String getName();
	/** @model */
	public String getType();				// return data type of the port
	public String dataToString(int data);	// interpret data in correct type and return as string
	public OutputPort clone(Element newParent);
}
