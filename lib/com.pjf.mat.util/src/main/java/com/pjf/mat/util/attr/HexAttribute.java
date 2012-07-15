package com.pjf.mat.util.attr;

import com.pjf.mat.api.AttributeType;
import com.pjf.mat.util.Conversion;

public class HexAttribute extends IntegerAttribute {

	public HexAttribute(String name, int configId) {
		super(name, configId);
	}
	
	@Override
	public String getValue() {
		return Conversion.toHexIntString(value);
	}

	@Override
	public void setValue(String value) {
		this.value = Integer.parseInt(value,16);
	}

	@Override
	public IntegerAttribute clone() {
		HexAttribute attr = new HexAttribute(getName(),getConfigId());
		attr.value = value;
		return attr;
	}

	@Override
	public AttributeType getType() {
		return AttributeType.HEX;
	}



}
