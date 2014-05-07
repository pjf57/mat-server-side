package com.cs.fwk.util.attr;

import org.apache.log4j.Logger;

import com.cs.fwk.api.AttrSysType;
import com.cs.fwk.api.AttributeType;
import com.cs.fwk.api.Element;
import com.cs.fwk.util.Conversion;

public class HexAttribute extends IntegerAttribute {
	private final static Logger logger = Logger.getLogger(HexAttribute.class);

	public HexAttribute(Element el, String name, int configId, AttrSysType sysType, String defaultStr, int order, String calcSpec) throws Exception {
		super(el, name, configId, sysType,order,calcSpec);
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
			attr = new HexAttribute(newParent,getName(),getConfigId(),getSysType(),getValue(),getOrder(),getCalcSpecs());
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
