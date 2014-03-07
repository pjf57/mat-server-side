package com.pjf.mat.impl.element;

import com.pjf.mat.api.Cmd;
import com.pjf.mat.api.Element;

public class BasicCmd extends RawCmd {
	private final boolean toAllCBs;
	private final Element parent;	// maybe null if toAllCBs is true
	
	/**
	 * Construct a command with specified parent, name, and configID
	 * 
	 * @param parent
	 * @param name
	 * @param configId
	 */
	public BasicCmd(Element parent, String name, int configId) {
		super(name,configId);
		this.parent = parent;
		toAllCBs = false;
	}

	/**
	 * Construct a command with specified parent, name, configID, and data
	 * 
	 * @param parent
	 * @param name
	 * @param configId
	 * @param data
	 */
	public BasicCmd(Element parent, String name, int configId, int data) {
		super(name,configId,data,0);
		this.parent = parent;
		toAllCBs = false;
	}

	/**
	 * Construct a command from another command, but with a specified parent
	 * @param parent
	 * @param cmd
	 */
	public BasicCmd(Element parent, Cmd cmd) {
		super(cmd);
		this.parent = parent;
		toAllCBs = false;
	}

	/**
	 * Create a command with data.
	 * 
	 * @param cmd command to clone from
	 * @param data data to send with command
	 */
	public BasicCmd(Cmd cmd, int data) {
		super(cmd,data);
		parent = cmd.getParent();
		toAllCBs = parent == null;
	}		
	
	public BasicCmd(Element parent, String name, int cfgId, int data, int arg) {
		super(name,cfgId,data,arg);
		this.parent = parent;
		toAllCBs = false;
	}

	/**
	 * Create a command to send to all CBs
	 * 
	 * @param name
	 * @param configId
	 * @param data
	 */
	public BasicCmd(String name, int configId, int data) {
		super(name,configId,data,0);
		this.parent = null;
		toAllCBs = true;
	}

	@Override
	public Element getParent() {
		return parent;
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer(getName());
		buf.append("[c"); buf.append(this.getConfigId()); buf.append(']');
		return buf.toString();
	}

	@Override
	public String getFullName() {
		if (toAllCBs) {
			return "[ALL] " + getName();
		}
		return parent.getId() + "/" + parent.getType() + ":" + getName();
	}


	@Override
	public int getParentID() {
		if (toAllCBs) {
			return 0xff;
		}
		return parent.getId();
	}


}
