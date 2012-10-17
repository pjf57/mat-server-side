package com.pjf.mat.util.attr;

import com.pjf.mat.api.AttrSysType;
import com.pjf.mat.api.AttributeType;


public class IntegerAttribute extends StringAttribute {
	protected int value;
	
	public IntegerAttribute(String name, int configId, AttrSysType sysType) {
		super(name,configId,sysType);
		value = 0;
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
		IntegerAttribute attr = new IntegerAttribute(getName(),getConfigId(),getSysType());
		attr.value = value;
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
