package com.cs.fwk.core.impl.element;

import com.cs.fwk.api.Element;
import com.cs.fwk.api.OutputPort;

public class IntegerOutputPort extends BasicOutputPort {

	public IntegerOutputPort(Element parent, int id, String name, String type) {
		super(parent, id, name, type);
	}

	public IntegerOutputPort(Element parent, OutputPort op) {
		super(parent, op);
	}

	@Override
	public OutputPort clone() {
		IntegerOutputPort op = new IntegerOutputPort(parent, getId(), name, type);
		return op;
	}
	
	@Override
	public String dataToString(int data) {
		return 	Integer.toString(data);
	}
}
