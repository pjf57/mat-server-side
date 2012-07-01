package com.pjf.mat.api;

/** @model */
public interface Attribute extends Cloneable {
	/** @model */
	public AttributeType getType();
	
	/** @model */
	public String getName();
	/** @model */
	public String getValue();
	/** @model */
	public int getConfigId();
	
	public void setValue(String value);
	
	/** @model */
	public Attribute clone();	
	/** @model changeable="false" volatile="true" */
	public int getEncodedData() throws Exception;	// return data as 32 bit int
}
