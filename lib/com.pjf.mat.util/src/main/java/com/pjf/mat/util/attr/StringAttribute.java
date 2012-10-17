package com.pjf.mat.util.attr;

import java.util.SortedSet;

import com.pjf.mat.api.AttrSysType;
import com.pjf.mat.api.Attribute;
import com.pjf.mat.api.AttributeType;
import com.pjf.mat.api.EnumValue;

public class StringAttribute implements Attribute, Cloneable {
	private final String name;
	private String value;
	private final AttrSysType sysType;
	private final int configId;
	
	public StringAttribute(String name, int configId, AttrSysType sysType) {
		this.name = name;
		this.configId = configId;
		this.sysType = sysType;
		this.value = "";
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void setValue(String value) throws Exception {
		this.value = value;
	}
	
	@Override
	public StringAttribute clone() {
		StringAttribute attr = new StringAttribute(name,configId,sysType);
		attr.value = null;
		return attr;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer(name);
		buf.append('='); buf.append(this.getValue());
		buf.append("[c"); buf.append(this.getConfigId()); buf.append(']');
		return buf.toString();
	}

	@Override
	public int getConfigId() {
		return configId;
	}

	@Override
	public int getEncodedData() throws Exception {
		String data;
		if (value == null) {
			data = "";
		} else {
			data = value;
		}
		if (data.length() > 4) {
			throw new Exception("String attribute value too long: " + this.toString());
		}
		int dataVal = 0;
		for (int i=0; i<4; i++) {
			byte b;
			if (i < data.length()) {
				b = (byte)data.charAt(i);
			} else {
				b = 0;
			}
			dataVal = 16 * dataVal + b;	
		}
		return dataVal;
	}

	@Override
	public AttributeType getType() {
		return AttributeType.STR;
	}

	@Override
	public SortedSet<EnumValue> getEnumValues() {
		return null;	// simple attributes dont have enum values
	}

	@Override
	public AttrSysType getSysType() {
		return sysType;
	}

}
