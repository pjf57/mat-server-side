package com.pjf.mat.util.attr;

import org.apache.log4j.Logger;

import com.pjf.mat.api.AttrSysType;
import com.pjf.mat.api.AttributeType;


public class IntegerAttribute extends StringAttribute {
	private final static Logger logger = Logger.getLogger(IntegerAttribute.class);
	protected int value;

	public IntegerAttribute(String name, int configId, AttrSysType sysType) throws Exception {
		super(name,configId,sysType);
		value = 0;
	}

	public IntegerAttribute(String name, int configId, AttrSysType sysType, String defaultStr) throws Exception {
		super(name,configId,sysType);
		value = 0;
		if (defaultStr != null) {
			value = Integer.parseInt(defaultStr);
		}
	}
	
	@Override
	public String getValue() {
		return Integer.toString(value);
	}

	@Override
	public void setValue(String value) throws Exception {
		this.value = Integer.parseInt(value);
	}

	@Override
	public IntegerAttribute clone() {
		IntegerAttribute attr = null;
		try {
			attr = new IntegerAttribute(getName(),getConfigId(),getSysType(),getValue());
		} catch (Exception e) {
			logger.error("Unable to set default value [" + getValue() + "] on [" + this + "]");
		}
		return attr;
	}

	@Override
	public int getEncodedData() throws Exception {
		return value;
	}

	@Override
	public AttributeType getType() {
		return AttributeType.INT;
	}


}
