package com.pjf.mat.util.attr;

import com.pjf.mat.api.AttrSysType;
import com.pjf.mat.api.AttributeType;


public class FloatAttribute extends StringAttribute {
	private float value;

	public FloatAttribute(String name, int configId, AttrSysType sysType) {
		super(name,configId,sysType);
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
		FloatAttribute attr = new FloatAttribute(getName(),getConfigId(),getSysType());
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
