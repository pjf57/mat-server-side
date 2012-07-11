package com.pjf.mat.impl.element;

import com.pjf.mat.api.Cmd;
import com.pjf.mat.api.Element;

public class BasicCmd implements Cmd {
	private final Element parent;
	private final String name;
	private final int configId;
	
	public BasicCmd(Element parent, String name, int configId) {
		this.parent = parent;
		this.name = name;
		this.configId = configId;
	}

	public BasicCmd(Element parent, Cmd cmd) {
		this.parent = parent;
		this.name = cmd.getName();
		this.configId = cmd.getConfigId();
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Element getParent() {
		return parent;
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer(name);
		buf.append("[c"); buf.append(this.getConfigId()); buf.append(']');
		return buf.toString();
	}

	@Override
	public int getConfigId() {
		return configId;
	}

	@Override
	public String getFullName() {
		return parent.getId() + "/" + parent.getType() + ":" + name;
	}


}
