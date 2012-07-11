package com.pjf.mat.impl.element;

import com.pjf.mat.api.Element;
import com.pjf.mat.api.OutputPort;

public class FloatOutputPort extends BasicOutputPort {

	public FloatOutputPort(Element parent, int id, String name, String type) {
		super(parent, id, name, type);
	}

	public FloatOutputPort(Element parent, OutputPort op) {
		super(parent, op);
	}

	@Override
	public OutputPort clone(Element newParent) {
		FloatOutputPort op = new FloatOutputPort(newParent, id, name, type);
		return op;
	}
	
	@Override
	public String dataToString(int data) {
		float f = Float.intBitsToFloat(data);
		return 	Float.toString(f);
	}

}
