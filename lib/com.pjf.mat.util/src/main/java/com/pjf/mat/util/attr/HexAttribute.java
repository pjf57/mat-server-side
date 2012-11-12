package com.pjf.mat.util.attr;

import org.apache.log4j.Logger;

import com.pjf.mat.api.AttrSysType;
import com.pjf.mat.api.AttributeType;
import com.pjf.mat.util.Conversion;

public class HexAttribute extends IntegerAttribute {
	private final static Logger logger = Logger.getLogger(HexAttribute.class);

	public HexAttribute(String name, int configId, AttrSysType sysType, String defaultStr) throws Exception {
		super(name, configId, sysType);
		if (defaultStr != null) {
			setValue(defaultStr);
		}
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
		HexAttribute attr = null;
		try {
			attr = new HexAttribute(getName(),getConfigId(),getSysType(),getValue());
		} catch (Exception e) {
			logger.error("Unable to set default value [" + getValue() + "] on [" + this + "]");
		}
		return attr;
	}

	@Override
	public AttributeType getType() {
		return AttributeType.HEX;
	}



}
