package com.pjf.mat.impl.element;

import com.pjf.mat.api.Element;
import com.pjf.mat.api.OutputPort;

public class IntegerOutputPort extends BasicOutputPort {

	public IntegerOutputPort(Element parent, int id, String name, String type) {
		super(parent, id, name, type);
	}

	public IntegerOutputPort(Element parent, OutputPort op) {
		super(parent, op);
	}

	@Override
	public OutputPort clone() {
		IntegerOutputPort op = new IntegerOutputPort(parent, id, name, type);
		return op;
	}
	
	@Override
	public String dataToString(int data) {
		return 	Integer.toString(data);
	}
}
