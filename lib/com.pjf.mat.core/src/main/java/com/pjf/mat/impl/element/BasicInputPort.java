package com.pjf.mat.impl.element;

import com.pjf.mat.api.InputPort;
import com.pjf.mat.api.Item;
import com.pjf.mat.api.OutputPort;

public class BasicInputPort extends BasicItem implements InputPort {
	private final String name;
	private final String type;
	private OutputPort src;


	public BasicInputPort(int id, String name, String type) {
		super(id);
		this.name = name;
		this.type = type;
		this.src = null;
	}

	public BasicInputPort(InputPort ip) {
		super(ip.getId());
		this.name = ip.getName();
		this.type = ip.getType();
		this.src = null;
	}


	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public String getType() {
		return type;
	}


	@Override
	public void connectTo(OutputPort src) {
		this.src = src;
	}

	@Override
	public void removeCxn() {
		this.src = null;
	}

	@Override
	public OutputPort getConnectedSrc() {
		return src;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(getId());
		buf.append(':'); buf.append(name);
		if (src != null) {
			buf.append("<="); buf.append(src.getParent().getId());
			buf.append(':'); buf.append(src.getName());
		}
		return buf.toString();
	}
	
	@Override
	public int compareTo(Item other) {
		return this.getId() - other.getId();
	}

}
