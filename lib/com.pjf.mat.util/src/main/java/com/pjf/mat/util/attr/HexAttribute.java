package com.pjf.mat.util.attr;

import org.apache.log4j.Logger;

import com.pjf.mat.api.AttrSysType;
import com.pjf.mat.api.AttributeType;
import com.pjf.mat.api.Element;
import com.pjf.mat.util.Conversion;

public class HexAttribute extends IntegerAttribute {
	private final static Logger logger = Logger.getLogger(HexAttribute.class);

	public HexAttribute(Element el, String name, int configId, AttrSysType sysType, String defaultStr) throws Exception {
		super(el, name, configId, sysType);
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
		this.value = (int) (Long.parseLong(value,16) & 0xffffffff);
	}

	@Override
	public IntegerAttribute clone(Element newParent) {
		HexAttribute attr = null;
		try {
			attr = new HexAttribute(newParent,getName(),getConfigId(),getSysType(),getValue());
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
