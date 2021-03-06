package com.cs.fwk.core.sys;

import java.util.Properties;

import com.cs.fwk.api.Attribute;
import com.cs.fwk.api.Element;
import com.cs.fwk.api.MatModel;
import com.cs.fwk.core.sys.MatPropertiesHelper.MatPropertyProcessor;

public class MatSystemLoader {
	
	
	private final MatPropertiesHelper helper;
	
	public MatSystemLoader(MatPropertiesHelper helper) {
		this.helper = helper;
	}
	
	public MatSystemLoader(Properties properties) {
		this(new MatPropertiesHelper(properties));
	}
	
	public void initialize(final MatModel mat) throws Exception {
		helper.processElementProperties(new MatPropertyProcessor() {
			int currentId = -1;
			Element srcElement = null;
			@Override
			public void process(int id, String key, String value) throws Exception {
				if (id != currentId) {
					srcElement = mat.getElement(id);
					currentId = id;
				}
				
				// connection
				if (helper.isInPortDefinition(key)) {
					int in = helper.getInPortIdFromProperty(key);
					int out = helper.getOutPortIdFromProperty(value);
					int target = helper.getElementIdFromProperty(value);
					Element tgtElement = mat.getElement(target);
					if (tgtElement == null) {
						throw new Exception("Error loading for id=" + id + ", key=" + key + ", value=" + value +
								", srcElement=" + srcElement.getShortName() + " tgtElement is null for target=" + target);
					}
					srcElement.getInputs().get(in).connectTo(tgtElement.getOutputs().get(out));
				}
				
				// parameter
				if (helper.isParamDefinition(key)) {
					int eltId = helper.getParamIdFromProperty(key);
					String name = helper.getParamNameFromProperty(key);
					Element tgtElement = mat.getElement(eltId);
					if (tgtElement != null) {
						Attribute attr = tgtElement.getAttribute(name);
						if (attr == null) {
							throw new Exception("No attribute [" + name + "] found in " + tgtElement);
						}
						attr.setValue(value);
					}
				}
			}
		});
		
	}

//	public static int getOutPortIdFromProperty(String name) {
//		return getPortIdFromProperty(regexOutput, name);
//	}
//	
//	public static int getInPortIdFromProperty(String name) {
//		return getPortIdFromProperty(regexInput, name);
//	}
//	
//	public static int getPortIdFromProperty(Pattern pattern, String name) {
//		Matcher matcher = pattern.matcher(name);
//		if (matcher.find()) {
//			String port = matcher.group(2);
//			return Integer.valueOf(port);
//		}
//		return -1;
//	}
//	
//	public static int getElementIdFromProperty(String name) {
//		if (name.startsWith(PREFIX_ELEMENT)) {
//			name = name.substring(PREFIX_ELEMENT.length()+1);
//		}
//		String id = name.substring(0, name.indexOf('.'));
//		return Integer.valueOf(id);
//	}
//	
//	public static Element getElementFromProperty(MatApi mat, String name) {
//		String id = name.substring(0, name.indexOf('.'));
//		Element element = mat.getModel().getElement(Integer.valueOf(id));
//		return element;
//	}
//	
//	public final String getProperty(String property) {
//		return getPropertyWithDefault(property, null);
//	}
//	
//	public final String getPropertyWithDefault(String property, String defaultValue) {
//		return properties.getProperty(property, defaultValue);
//	}
//	
//	public final String getProperty (String prefix, String property) {
//		return getPropertyWithDefault(prefix, property, null);
//	}
//	
//	public final String getPropertyWithDefault(String prefix, String property, String defaultValue) {
//		prefix = prefix.charAt(prefix.length()-1) == '.' ? prefix : prefix.substring(0, prefix.length()) + ".";
//		String key = prefix + property;
//		return getPropertyWithDefault(key, defaultValue);
//	}
//
//	
//	public final String[] getPropertyNames(String prefix) {
//		return getPropertyNames(properties, prefix);
//	}
//
//	public static final String[] getPropertyNames(Properties properties, String prefix) {
//		prefix = prefix.charAt(prefix.length()-1) == '.' ? prefix.substring(0, prefix.length()-1) :  prefix;
//		List<String>names = new ArrayList<String>();
//		for(Enumeration<?> keys = properties.propertyNames(); keys.hasMoreElements(); ) {
//			Object object = keys.nextElement();
//			String key = object.toString();
//			if (key.startsWith(prefix)) {
//				String name = key.substring(prefix.length());
//				names.add(name.charAt(0) == '.' ? name.substring(1) : name); 
//			}
//		}
//		return names.toArray(new String[names.size()]);
//	}

	public final Properties getProperties() {
		return helper.getProperties();
	}
}

