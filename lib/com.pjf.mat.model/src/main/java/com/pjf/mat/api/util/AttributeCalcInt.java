package com.pjf.mat.api.util;

import com.pjf.mat.api.Element;

/**
 * Interface for classes that provide implementations of calculators
 * to calculate the values of calculated attributes
 */
public interface AttributeCalcInt {
	
	/**
	 * Calculate value of the named attr for the given element
	 * Store the new value in place in the element
	 * Throw exception if an error occurs
	 */
	public void calculate(String attrName, Element el, String arg) throws Exception;

}
