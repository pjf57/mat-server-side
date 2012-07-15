package com.pjf.mat.sys;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pjf.mat.api.Element;
import com.pjf.mat.api.MatApi;

public class MatSystemLoader {
	
	public static final String PREFIX = "diagram.element";
	private static final Pattern regexInput = Pattern.compile("(\\d*)\\.in\\.(\\d*)");
	private static final Pattern regexOutput = Pattern.compile("(\\d*)\\.out\\.(\\d*)");

	protected Properties properties;
	
	public MatSystemLoader(Properties properties) {
		this.properties = properties;
	}
	
	public void initialize(MatApi mat) {
		String[] names = getPropertyNames(PREFIX);
		Arrays.sort(names);
		int currentId = -1;
		Element srcElement = null;
		for(String name : names) {
			int id = getElementIdFromProperty(name);
			if (id != currentId) {
				srcElement = mat.getElement(id);
			}
			
			if (name.startsWith(id + ".in.")) {
				int in = getInPortIdFromProperty(name);
				String value = getProperty(PREFIX, name);
				int out = getOutPortIdFromProperty(value);				
				int target = getElementIdFromProperty(value);
				Element tgtElement = mat.getElement(target);
				srcElement.getInputs().get(in).connectTo(tgtElement.getOutputs().get(out));
			} else if (name.startsWith("loc.")) {
				
			}
		}
	}

	protected int getOutPortIdFromProperty(String name) {
		return getPortIdFromProperty(regexOutput, name);
	}
	
	protected int getInPortIdFromProperty(String name) {
		return getPortIdFromProperty(regexInput, name);
	}
	
	protected int getPortIdFromProperty(Pattern pattern, String name) {
		Matcher matcher = pattern.matcher(name);
		if (matcher.find()) {
			String port = matcher.group(2);
			return Integer.valueOf(port);
		}
		return -1;
	}
	
	protected int getElementIdFromProperty(String name) {
		String id = name.substring(0, name.indexOf('.'));
		return Integer.valueOf(id);
	}
	
	protected Element getElementFromProperty(MatApi mat, String name) {
		String id = name.substring(0, name.indexOf('.'));
		Element element = mat.getElement(Integer.valueOf(id));
		return element;
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
	
	public final String[] getPropertyNames(String prefix) {
		prefix = prefix.charAt(prefix.length()-1) == '.' ? prefix.substring(0, prefix.length()-1) :  prefix;
		List<String>names = new ArrayList<String>();
		for(Enumeration<Object> keys = properties.keys(); keys.hasMoreElements(); ) {
			Object object = keys.nextElement();
			String key = object.toString();
			if (key.startsWith(prefix)) {
				String name = key.substring(prefix.length());
				names.add(name.charAt(0) == '.' ? name.substring(1) : name); 
			}
		}
		return names.toArray(new String[names.size()]);
	}

}
