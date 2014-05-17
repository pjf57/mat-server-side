package com.cs.fwk.core.config.calculators;

import com.cs.fwk.api.Element;
import com.cs.fwk.api.util.AttributeCalcInt;


/**
 * Calculate oper value for A4IP by or-in together the values for P, Q, and Z
 * 
 * @author pjf
 *
 */
public class A4IPOperCalc extends BaseAttributeCalculator implements AttributeCalcInt {

	@Override
	public void calculate(String attrName, Element el, String arg)
			throws Exception {
		initialise(attrName,el);
		int p = getRawVal("P");
		int q = getRawVal("Q");
		int z = getRawVal("Z");
		int oper = p | q | z;
		setValueHex(oper);
	}
}
