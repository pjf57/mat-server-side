package com.pjf.mat.util.attr;

import org.apache.log4j.Logger;

import com.pjf.mat.api.AttrSysType;
import com.pjf.mat.api.AttributeType;
import com.pjf.mat.api.Element;


public class FloatAttribute extends StringAttribute {
	private final static Logger logger = Logger.getLogger(FloatAttribute.class);
	private float value;

	public FloatAttribute(Element el, String name, int configId, AttrSysType sysType, String defaultStr, int order) throws Exception {
		super(el,name,configId,sysType,order);
		value = 0;
		if (defaultStr != null) {
			setValue(defaultStr);
		}
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
	public FloatAttribute clone(Element newParent) {
		FloatAttribute attr = null;
		try {
			attr = new FloatAttribute(newParent,getName(),getConfigId(),getSysType(),getValue(),getOrder());
		} catch (Exception e) {
			logger.error("Unable to set default value [" + getValue() + "] on [" + this + "]");
		}
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
