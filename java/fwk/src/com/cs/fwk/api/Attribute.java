package com.cs.fwk.api;

import java.util.List;
import java.util.SortedSet;

import com.cs.fwk.api.util.ConfigItem;

/**
 * Model of an Element's attribute
 * 
 * An attribute may be directly associated with a CB attribute in the HW, or may be a pseudo attribute.
 * Attributes have a type which indicates how data for them is encoded or decoded.
 * 
 * @author pjf
 *
 */
public interface Attribute extends Cloneable {

	/**
	 * @return name of the attribute
	 */
	public String getName();

	/**
	 * @return type of Attribute (int, float, enum, ...)
	 */
	public AttributeType getType();

	/**
	 * Set the value of the attribute
	 * 
	 * @param value - to be interpreted according to attr type
	 * @throws Exception - if error parsing the string
	 */
	public void setValue(String value) throws Exception;

	/** 
	 * @return value of attr (interpreted according to attr type)
	 */
	public String getValue();

	/**
	 * @return raw value of attr
	 */
	public int getRawValue();
	
	/**
	 * @return HW config ID of attr
	 */
	public int getConfigId();
	
	/**
	 * @return display order of attr
	 */
	public int getOrder();
	
	/**
	 * @return system-type (norm, sys, LKU-target) of attr
	 */
	public AttrSysType getSysType();
	
	/**
	 * @return Element which holds this attr
	 */
	public Element getParent();
	
	/**
	 * @return sorted set of enum values (or null if not an enum)
	 */
	public SortedSet<EnumValue> getEnumValues();
	
	/**
	 * Return a copy of the attribute, but with the specified parent
	 * 
	 * @param newParent
	 * @return cloned copy of attr
	 */
	public Attribute clone(Element newParent);	

	/**
	 * @return 32 bit int representation of the attr value as required for tx to HW
	 * @throws Exception if there was an error encoding
	 */
	public int getEncodedData() throws Exception;	// return data as 32 bit int

	/** 
	 * @return list of config items to tx to HW to configure this attribute 
	 * @throws Exception if error occured making the list
	 */
	public List<ConfigItem> getConfigList() throws Exception;

	/**
	 * @return true if the attribute is a calculated attribute
	 */
	public boolean isCalculated();
	
	/**
	 * @return the calculation specs for the attribute, or null
	 */
	public String getCalcSpecs();

}
