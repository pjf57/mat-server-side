package com.cs.fwk.util.attr;

import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.cs.fwk.api.AttrSysType;
import com.cs.fwk.api.AttributeType;
import com.cs.fwk.api.Element;
import com.cs.fwk.api.EnumValue;

/**
 * An attribute that models an integer with enumerated values
 * 
 * @author pjf
 */
public class EnumAttribute extends IntegerAttribute {
	private final static Logger logger = Logger.getLogger(EnumAttribute.class);
	private SortedSet<EnumValue> values;

	public EnumAttribute(Element el, String name, int configId, AttrSysType sysType, int order, String calcSpec) throws Exception {
		super(el, name, configId, sysType,order,calcSpec);
		values = new TreeSet<EnumValue>();
	}
	
	public void addEnumValue(EnumValue ev) {
		values.add(ev);
	}
	
	@Override
	public String getValue() {
		EnumValue ev = lookupByValue(value);
		if (ev == null) {
			return "";
		}
		return ev.getName();
	}
	
	@Override
	public int getRawValue() {
		EnumValue ev = lookupByValue(value);
		if (ev == null) {
			return -1;
		}
		return ev.getValue();
	}



	@Override
	public void setValue(String value) throws Exception {
		EnumValue ev = lookupByName(value);
		if (ev == null) {
			throw new Exception("No enum value for [" + value + "] in attr " + getName());
		}
		this.value = ev.getValue();
	}

	/**
	 * @param name
	 * @return EnumValueue from enum values list or null
	 */
	private EnumValue lookupByName(String name) {
		EnumValue v = null;
		for (EnumValue ev : values) {
			if (ev.getName().equals(name)) {
				v = ev;
				break;
			}
		}
		return v;
	}
	
	/**
	 * @param integer value
	 * @return EnumValueue from enum values list or null
	 */
	private EnumValue lookupByValue(int value) {
		EnumValue v = null;
		for (EnumValue ev : values) {
			if (ev.getValue() == value) {
				v = ev;
				break;
			}
		}
		return v;
	}


	@Override
	public EnumAttribute clone(Element newParent) {
		EnumAttribute attr = null;
		try {
			attr = new EnumAttribute(newParent,getName(),getConfigId(),getSysType(),getOrder(),getCalcSpecs());
			attr.value = value;
			attr.values = values;
		} catch (Exception e) {
			logger.error("Unable to set default value [" + getValue() + "] on [" + this + "]");
		}
		return attr;
	}

	@Override
	public AttributeType getType() {
		return AttributeType.ENUM;
	}
	
	
	@Override
	public SortedSet<EnumValue> getEnumValues() {
		return values;
	}


}
