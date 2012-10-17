package com.pjf.mat.util.attr;

import com.pjf.mat.api.AttrSysType;
import com.pjf.mat.api.AttributeType;
import com.pjf.mat.util.Conversion;

public class HexAttribute extends IntegerAttribute {

	public HexAttribute(String name, int configId, AttrSysType sysType) {
		super(name, configId, sysType);
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
		HexAttribute attr = new HexAttribute(getName(),getConfigId(),getSysType());
		attr.value = value;
		return attr;
	}

	@Override
	public AttributeType getType() {
		return AttributeType.HEX;
	}



}
