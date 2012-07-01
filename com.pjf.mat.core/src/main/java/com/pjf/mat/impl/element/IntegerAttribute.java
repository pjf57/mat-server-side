package com.pjf.mat.impl.element;

import com.pjf.mat.api.AttributeType;


public class IntegerAttribute extends StringAttribute {
	protected int value;
	
	public IntegerAttribute(String name, int configId) {
		super(name,configId);
		value = 0;
	}
	
	@Override
	public String getValue() {
		return Integer.toString(value);
	}

	@Override
	public void setValue(String value) {
		this.value = Integer.parseInt(value);
	}

	@Override
	public IntegerAttribute clone() {
		IntegerAttribute attr = new IntegerAttribute(getName(),getConfigId());
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
