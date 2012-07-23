package com.pjf.mat.api;

/** @model */
public interface OutputPort extends Cloneable, Port {
	/** @model opposite="outputs"*/
	public Element getParent();
	/** @model */
	public String getName();
	public String dataToString(int data);	// interpret data in correct type and return as string
	public OutputPort clone(Element newParent);
}
