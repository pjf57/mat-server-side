package com.cs.fwk.api.util;


import com.cs.fwk.api.Element;

/**
 * Interface for classes that provide implementations of calculators
 * to determine config text for a CB
 */
public interface ConfigTextCalcInt {
	
	/**
	 * Calculate the list of strings that describe the current config of a CB
	 * Throw exception if an error occurs
	 */
	public CBConfigText calculate(Element el) throws Exception;

}
