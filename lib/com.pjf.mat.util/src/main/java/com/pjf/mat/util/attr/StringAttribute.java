package com.pjf.mat.util.attr;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

import org.apache.log4j.Logger;

import com.pjf.mat.api.AttrSysType;
import com.pjf.mat.api.Attribute;
import com.pjf.mat.api.AttributeType;
import com.pjf.mat.api.util.ConfigItem;
import com.pjf.mat.api.Element;
import com.pjf.mat.api.EnumValue;


public class StringAttribute implements Attribute, Cloneable {
	private final Element parent;
	private final static Logger logger = Logger.getLogger(StringAttribute.class);
	private final String name;
	private String value;
	private final AttrSysType sysType;
	private final int configId;
	private final int order;
	private final String calcSpec;
	
	public StringAttribute(Element el, String name, int configId, AttrSysType sysType, String defaultStr, int order, String calcSpec) throws Exception {
		this.parent = el;
		this.name = name;
		this.configId = configId;
		this.sysType = sysType;
		this.value = "";
		if (defaultStr != null) {
			setValue(defaultStr);
		}
		this.order = 99;
		this.calcSpec = calcSpec;
	}

	public StringAttribute(Element el, String name, int configId, AttrSysType sysType, int order, String calcSpec) throws Exception {
		this.parent = el;
		this.name = name;
		this.configId = configId;
		this.sysType = sysType;
		this.value = "";
		this.order = order;
		this.calcSpec = calcSpec;
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
	public int getOrder() {
		return order;
	}
	
	@Override
	public StringAttribute clone(Element newParent) {
		StringAttribute attr = null;
		try {
			attr = new StringAttribute(newParent,name,configId,sysType,getValue(),getOrder(),getCalcSpecs());
		} catch (Exception e) {
			logger.error("Unable to set default value [" + getValue() + "] on [" + this + "]");
		}
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
	
	public Element getParent() {
		return parent;
	}
	
	public List<ConfigItem> getConfigList() throws Exception {
		List<ConfigItem> configs = new ArrayList<ConfigItem>();
		ConfigItem cfg = new ConfigItem(parent.getId(),getSysType(), getConfigId(), getEncodedData());
		configs.add(cfg);
		return configs;
	}

	@Override
	public boolean isCalculated() {
		return calcSpec != null;
	}
	
	@Override
	public String getCalcSpecs() {
		return calcSpec;
	}

}
