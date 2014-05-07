package com.cs.fwk.core.impl.element;

import com.cs.fwk.api.Element;
import com.cs.fwk.api.OutputPort;
import com.cs.fwk.util.Conversion;


public class BasicOutputPort extends BasicItem implements OutputPort {
	protected final String name;
	protected final String type;
	protected final Element parent;

	public BasicOutputPort(Element parent, int id, String name, String type) {
		super(id);
		this.parent = parent;
		this.name = name;
		this.type = type;
	}


	public BasicOutputPort(Element parent, OutputPort op) {
		super(op.getId());
		this.parent = parent;
		this.name = op.getName();
		this.type = op.getType();
	}

	@Override
	public OutputPort clone(Element newParent) {
		BasicOutputPort op = new BasicOutputPort(newParent, getId(), name, type);
		return op;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(getId());
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
