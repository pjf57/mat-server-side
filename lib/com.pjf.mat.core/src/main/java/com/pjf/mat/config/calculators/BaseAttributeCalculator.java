package com.pjf.mat.config.calculators;

import com.pjf.mat.api.Attribute;
import com.pjf.mat.api.Element;

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
	 * Set value of target attribute
	 */
	protected void setValue(String value) throws Exception {
		target.setValue(value);
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

}
