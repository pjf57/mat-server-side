package com.pjf.mat.config.calculators;

import com.pjf.mat.api.Element;
import com.pjf.mat.api.util.AttributeCalcInt;


/**
 * Calculate alpha value for an EMA
 * 
 * @author pjf
 *
 */
public class EmaAlphaCalc extends BaseAttributeCalculator implements AttributeCalcInt {

	@Override
	public void calculate(String attrName, Element el, String arg)
			throws Exception {
		initialise(attrName,el);
		float len = getFloatVal(arg);
		float alpha = 2.0f / (len + 1);
		setValue("" + alpha);
	}

}
