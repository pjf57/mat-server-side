package com.cs.fwk.core.config.calculators;

import com.cs.fwk.api.Attribute;
import com.cs.fwk.api.Element;

public class BaseAttributeAccessor {

	protected Element el;

	public void initialise(Element el) {
		this.el = el;		
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