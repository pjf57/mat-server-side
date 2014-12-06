package com.cs.fwk.core.config.calculators;

import com.cs.fwk.api.Attribute;
import com.cs.fwk.api.Element;
import com.cs.fwk.util.Conversion;

/**
 * Provides services commonly used by attribute calculators
 */
public class BaseAttributeCalculator extends BaseAttributeAccessor {
	private Attribute target;
	
	/**
	 * Initialise the attribute calculator
	 * 
	 * @throws Exception 
	 * 
	 */
	protected void initialise(String targetAttrName,Element el) throws Exception {
		super.initialise(el);
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

	

}
