package com.cs.fwk.core.impl.element;

import com.cs.fwk.api.Cmd;
import com.cs.fwk.api.Element;

public abstract class RawCmd implements Cmd {
	private final String name;
	private final int configId;
	private final int arg;
	private final int data;
	
	/**
	 * Construct a command with specified name, and configID
	 * 
	 * @param name
	 * @param configId
	 */
	public RawCmd(String name, int configId) {
		this.name = name;
		this.configId = configId;
		this.arg = 0;
		this.data = 0;
	}

	/**
	 * Construct a command with specified name, configID, and data
	 * 
	 * @param name
	 * @param configId
	 * @param data
	 * @param arg 
	 */
	public RawCmd(String name, int configId, int data, int arg) {
		this.name = name;
		this.configId = configId;
		this.arg = arg;
		this.data = data;
	}


	/**
	 * Construct a command from another command
	 * 
	 * @param cmd
	 */
	public RawCmd(Cmd cmd) {
		this.name = cmd.getName();
		this.configId = cmd.getConfigId();
		this.arg = 0;
		this.data = cmd.getData();
	}

	/**
	 * Construct a command from another command but different data
	 * 
	 * @param cmd
	 * @param data
	 */
	public RawCmd(Cmd cmd, int data) {
		this.name = cmd.getName();
		this.configId = cmd.getConfigId();
		this.arg = 0;
		this.data = data;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Element getParent() {
		return null;
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
		return name;
	}

	@Override
	public int getData() {
		return data;
	}

	@Override
	public int getArg() {
		return arg;
	}

	@Override
	public int getParentID() {
		return -1;
	}


}
