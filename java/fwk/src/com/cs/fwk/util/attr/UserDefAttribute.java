package com.cs.fwk.util.attr;

import java.util.List;
import java.util.SortedSet;

import org.apache.log4j.Logger;

import com.cs.fwk.api.AttrSysType;
import com.cs.fwk.api.Attribute;
import com.cs.fwk.api.AttributeType;
import com.cs.fwk.api.util.ConfigItem;
import com.cs.fwk.api.Element;
import com.cs.fwk.api.EnumValue;
import com.cs.fwk.api.util.AttrConfigGenerator;


public class UserDefAttribute implements Attribute, Cloneable {
	private final static Logger logger = Logger.getLogger(UserDefAttribute.class);
	private static final String CONVERTER_PKG = "com.cs.fwk.core.config.converters";
	private final Element parent;
	private final String name;
	private String value;
	private final AttrSysType sysType;
	private final int configId;
	private String converter;
	private final int order;
	private final String calcSpec;
	
	public UserDefAttribute(Element parent, String name, int configId, String converter, AttrSysType sysType, String defaultStr, int order, String calcSpec) throws Exception {
		this.parent = parent;
		this.name = name;
		this.configId = configId;
		this.sysType = sysType;
		this.converter = converter;
		this.value = "";
		this.order = order;
		if (defaultStr != null) {
			setValue(defaultStr);
		}
		this.calcSpec = calcSpec;
	}

	public UserDefAttribute(Element el, String name, int configId, AttrSysType sysType, int order, String calcSpec) throws Exception {
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
	public UserDefAttribute clone(Element newParent) {
		UserDefAttribute attr = null;
		try {
			attr = new UserDefAttribute(newParent,name,configId,converter,sysType,getValue(),getOrder(),getCalcSpecs());
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
		throw new Exception("Not supported - use getConfigList()");
	}

	@Override
	public AttributeType getType() {
		return AttributeType.USERDEF;
	}

	@Override
	public SortedSet<EnumValue> getEnumValues() {
		return null;	// simple attributes dont have enum values
	}

	@Override
	public AttrSysType getSysType() {
		return sysType;
	}

	@Override
	public List<ConfigItem> getConfigList() throws Exception {
		String cn = CONVERTER_PKG + "." + converter;
		AttrConfigGenerator gen = (AttrConfigGenerator) ClassLoader.getSystemClassLoader().loadClass(cn).newInstance();
		List<ConfigItem> configs = gen.generate(this);
		return configs;
	}

	@Override
	public Element getParent() {
		return parent;
	}

	@Override
	public int getOrder() {
		return order;
	}

	@Override
	public boolean isCalculated() {
		return calcSpec != null;
	}
	
	@Override
	public String getCalcSpecs() {
		return calcSpec;
	}
	
	@Override
	public int getRawValue() {
		return 0;
	}


}
