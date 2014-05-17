package com.cs.fwk.core.impl.element;

import com.cs.fwk.api.Element;
import com.cs.fwk.api.OutputPort;

public class FloatOutputPort extends BasicOutputPort {

	public FloatOutputPort(Element parent, int id, String name, String type) {
		super(parent, id, name, type);
	}

	public FloatOutputPort(Element parent, OutputPort op) {
		super(parent, op);
	}

	@Override
	public OutputPort clone(Element newParent) {
		FloatOutputPort op = new FloatOutputPort(newParent, getId(), name, type);
		return op;
	}
	
	@Override
	public String dataToString(int data) {
		float f = Float.intBitsToFloat(data);
		return 	Float.toString(f);
	}

}
