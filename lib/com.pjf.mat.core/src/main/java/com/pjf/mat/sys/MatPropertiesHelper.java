package com.pjf.mat.sys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatPropertiesHelper {

	public static interface MatPropertyProcessor {
		public void process(int elementId, String key, String value);
	}
	
	// ------------------------------------------------------------------------
	
	public static final String PREFIX_ELEMENT = "diagram.element";
	private static final String PREFIX_TYPE = "type";
	
	private static final Pattern regexInput = Pattern.compile("(\\d*)\\.in\\.(\\d*)");
	private static final Pattern regexOutput = Pattern.compile("(\\d*)\\.out\\.(\\d*)");
	private static final Pattern regexParam = Pattern.compile("(\\d*)\\.prop\\.(.*)");

	protected final Properties properties;
	
	public MatPropertiesHelper(Properties properties) {
		this.properties = properties;
	}

	public final Properties getProperties() {
		return this.properties;
	}
	
	public final String getProperty(String property) {
		return getPropertyWithDefault(property, null);
	}
	
	public final String getPropertyWithDefault(String property, String defaultValue) {
		return properties.getProperty(property, defaultValue);
	}
	
	public final String getProperty (String prefix, String property) {
		return getPropertyWithDefault(prefix, property, null);
	}
	
	public final String getPropertyWithDefault(String prefix, String property, String defaultValue) {
		prefix = prefix.charAt(prefix.length()-1) == '.' ? prefix : prefix.substring(0, prefix.length()) + ".";
		String key = prefix + property;
		return getPropertyWithDefault(key, defaultValue);
	}

	public boolean isOutPortDefinition(String name) {
		name = filterName(name);
		Matcher m = regexOutput.matcher(name);
		return m.find();
	}
	
	public int getOutPortIdFromProperty(String name) {
		return getPortIdFromProperty(regexOutput, name, 2);
	}
	
	public boolean isParamDefinition(String name) {
		name = filterName(name);
		Matcher m = regexParam.matcher(name);
		return m.find();
	}
	
	public int getParamIdFromProperty(String name) {
		return getPortIdFromProperty(regexParam, name, 1);
	}
	
	public String getParamNameFromProperty(String name) {
		name = filterName(name);
		Matcher matcher = regexParam.matcher(name);
		if (matcher.find()) {
			String param = matcher.group(2);
			return param;
		}
		return null;
	}

	public boolean isInPortDefinition(String name) {
		name = filterName(name);
		Matcher m = regexInput.matcher(name);
		return m.find();
	}
	
	public int getInPortIdFromProperty(String name) {
		return getPortIdFromProperty(regexInput, name, 2);
	}
	
	public String getElementType(int elementId) {
		String typeKey = PREFIX_ELEMENT + "." + elementId + "." + PREFIX_TYPE;
		return getProperty(typeKey);
	}
	
	public String getElementProperty(int elementId, String name) {
		String typeKey = PREFIX_ELEMENT + "." + elementId;
		return getProperty(typeKey, name);
	}
	
	public int getElementIdFromProperty(String name) {
		name = filterName(name);
		String id = name.substring(0, name.indexOf('.'));
		return Integer.valueOf(id);
	}

//	public static Element getElementFromProperty(MatApi mat, String name) {
//		String id = name.substring(0, name.indexOf('.'));
//		Element element = mat.getModel().getElement(Integer.valueOf(id));
//		return element;
//	}

	public void processElementProperties(MatPropertyProcessor processor) {
		String[] names = getPropertyNames(PREFIX_ELEMENT);
		Arrays.sort(names);
		for(String name: names) {
			int id = getElementIdFromProperty(name);
			String value = getProperty(PREFIX_ELEMENT, name);
			processor.process(id, name, value);
		}
	}

	public String[] getElementPropertyNames() {
		return getPropertyNames(PREFIX_ELEMENT);
	}
	
	public final String[] getPropertyNames(/*Properties properties,*/ String prefix) {
		prefix = prefix.charAt(prefix.length()-1) == '.' ? prefix.substring(0, prefix.length()-1) :  prefix;
		List<String>names = new ArrayList<String>();
		for(Enumeration<?> keys = properties.propertyNames(); keys.hasMoreElements(); ) {
			Object object = keys.nextElement();
			String key = object.toString();
			if (key.startsWith(prefix)) {
				String name = key.substring(prefix.length());
				names.add(name.charAt(0) == '.' ? name.substring(1) : name); 
			}
		}
		return names.toArray(new String[names.size()]);
	}

	// -------------------
	
	protected int getPortIdFromProperty(Pattern pattern, String name, int position) {
		name = filterName(name);
		Matcher matcher = pattern.matcher(name);
		if (matcher.find()) {
			String port = matcher.group(position);
			return Integer.valueOf(port);
		}
		return -1;
	}

	protected String filterName(String name) {
		if (name.startsWith(PREFIX_ELEMENT)) {
			name = name.substring(PREFIX_ELEMENT.length()+1);
		}
		return name;
	}

}

