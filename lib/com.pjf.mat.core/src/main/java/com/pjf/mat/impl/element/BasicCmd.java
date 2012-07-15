package com.pjf.mat.impl.element;

import com.pjf.mat.api.Cmd;
import com.pjf.mat.api.Element;

public class BasicCmd implements Cmd {
	private final Element parent;
	private final String name;
	private final int configId;
	private final int data;
	
	public BasicCmd(Element parent, String name, int configId) {
		this.parent = parent;
		this.name = name;
		this.configId = configId;
		this.data = 0;
	}

	public BasicCmd(Element parent, Cmd cmd) {
		this.parent = parent;
		this.name = cmd.getName();
		this.configId = cmd.getConfigId();
		this.data = 0;
	}

	/**
	 * Create a command with data.
	 * 
	 * @param cmd command to clone from
	 * @param data data to send with command
	 */
	public BasicCmd(Cmd cmd, int data) {
		this.parent = cmd.getParent();
		this.name = cmd.getName();
		this.configId = cmd.getConfigId();
		this.data = data;
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

	@Override
	public int getData() {
		return data;
	}


}
