package com.pjf.mat.impl.element;

import com.pjf.mat.api.AttributeType;


public class FloatAttribute extends StringAttribute {
	private float value;

	public FloatAttribute(String name, int configId) {
		super(name,configId);
		value = 0;
	}
	
	@Override
	public String getValue() {
		return Float.toString(value);
	}

	@Override
	public void setValue(String value) {
		this.value = Float.parseFloat(value);
	}

	@Override
	public FloatAttribute clone() {
		FloatAttribute attr = new FloatAttribute(getName(),getConfigId());
		attr.value = value;
		return attr;
	}

	@Override
	public int getEncodedData() throws Exception {
		int intBits = Float.floatToIntBits(value);
		return intBits;
	}

	@Override
	public AttributeType getType() {
		return AttributeType.HEX;
	}

}
