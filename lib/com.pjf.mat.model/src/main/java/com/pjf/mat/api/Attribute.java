package com.pjf.mat.api;

import java.util.SortedSet;

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
	
	public AttrSysType getSysType();
	
	public void setValue(String value) throws Exception;
	
	/**
	 * @return sorted set of enum values (or null if not an enum)
	 */
	public SortedSet<EnumValue> getEnumValues();
	
	/** @model */
	public Attribute clone();	
	/** @model changeable="false" volatile="true" */
	public int getEncodedData() throws Exception;	// return data as 32 bit int
}
