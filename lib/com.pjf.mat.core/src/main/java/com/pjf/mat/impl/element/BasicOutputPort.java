package com.pjf.mat.impl.element;

import com.pjf.mat.api.Element;
import com.pjf.mat.api.OutputPort;
import com.pjf.mat.impl.util.Conversion;

public class BasicOutputPort implements OutputPort {
	protected final int id;
	protected final String name;
	protected final String type;
	protected final Element parent;

	public BasicOutputPort(Element parent, int id, String name, String type) {
		this.parent = parent;
		this.id = id;
		this.name = name;
		this.type = type;
	}


	public BasicOutputPort(Element parent, OutputPort op) {
		this.parent = parent;
		this.id = op.getId();
		this.name = op.getName();
		this.type = op.getType();
	}

	@Override
	public OutputPort clone(Element newParent) {
		BasicOutputPort op = new BasicOutputPort(newParent, id, name, type);
		return op;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(id);
		buf.append(':'); buf.append(name);
		return buf.toString();
	}


	@Override
	public Element getParent() {
		return parent;
	}


	@Override
	public String getType() {
		return type;
	}


	@Override
	public String dataToString(int data) {
		return "0x" + Conversion.toHexIntString(data);
	}
	

}
