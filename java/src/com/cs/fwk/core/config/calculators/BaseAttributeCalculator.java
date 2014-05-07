package com.cs.fwk.core.config.calculators;

import com.cs.fwk.api.Attribute;
import com.cs.fwk.api.Element;
import com.cs.fwk.util.Conversion;

/**
 * Provides services commonly used by attribute calculators
 */
public class BaseAttributeCalculator {
	private Element el;
	private Attribute target;
	
	/**
	 * Initialise the attribute calculator
	 * 
	 * @throws Exception 
	 * 
	 */
	protected void initialise(String targetAttrName,Element el) throws Exception {
		this.el = el;
		target = el.getAttribute(targetAttrName);
		if (target == null) {
			throw new Exception("No such target attribute: " + targetAttrName + 
					" on element: " + el.getShortName());
		}
	}

	/**
	 * Set string value of target attribute
	 */
	protected void setValue(String value) throws Exception {
		target.setValue(value);
	}

	/**
	 * Set int value as hex
	 * 
	 * @param v
	 * @throws Exception 
	 */
	protected void setValueHex(int v) throws Exception {
		String val = Conversion.toHexIntString(v);
		target.setValue(val);
	}
	
	
	/**
	 * @return the float value of the named attribute
	 */
	protected float getFloatVal(String attrName) throws Exception {
		Attribute attr = el.getAttribute(attrName);
		if (attr == null) {
			throw new Exception("No such attribute: " + attrName + 
					" on element: " + el.getShortName());
		}
		String strVal = attr.getValue();
		float val = Float.parseFloat(strVal);
		return val;
	}

	/**
	 * @param attrName
	 * @return int value of the attr
	 * @throws Exception 
	 */
	protected int getIntVal(String attrName) throws Exception {
		Attribute attr = el.getAttribute(attrName);
		if (attr == null) {
			throw new Exception("No such attribute: " + attrName + 
					" on element: " + el.getShortName());
		}
		String strVal = attr.getValue();
		int val = Integer.parseInt(strVal);
		return val;
	}
	
	protected int getRawVal(String 	attrName) throws Exception {
		Attribute attr = el.getAttribute(attrName);
		if (attr == null) {
			throw new Exception("No such attribute: " + attrName + 
					" on element: " + el.getShortName());
		}
		int val = attr.getRawValue();
		return val;
	}

	

}
