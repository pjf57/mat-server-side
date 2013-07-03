package com.pjf.mat.config.calculators;

import com.pjf.mat.api.Element;
import com.pjf.mat.api.util.AttributeCalcInt;


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
