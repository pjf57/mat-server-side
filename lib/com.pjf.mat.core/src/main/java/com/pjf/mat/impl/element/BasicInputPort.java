package com.pjf.mat.impl.element;

import com.pjf.mat.api.InputPort;
import com.pjf.mat.api.OutputPort;

public class BasicInputPort implements InputPort {
	private final int id;
	private final String name;
	private final String type;
	private OutputPort src;


	public BasicInputPort(int id, String name, String type) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.src = null;
	}

	public BasicInputPort(InputPort ip) {
		this.id = ip.getId();
		this.name = ip.getName();
		this.type = ip.getType();
		this.src = null;
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
	public String getType() {
		return type;
	}


	@Override
	public void connectTo(OutputPort src) {
		this.src = src;
	}


	@Override
	public OutputPort getConnectedSrc() {
		return src;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(id);
		buf.append(':'); buf.append(name);
		if (src != null) {
			buf.append("<="); buf.append(src.getParent().getId());
			buf.append(':'); buf.append(src.getName());
		}
		return buf.toString();
	}


}
