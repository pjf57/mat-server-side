package com.cs.fwk.core.impl.element;

public class SystemCmd extends RawCmd {
	
	public final int SYSTEM_ID = 0;

	/**
	 * Construct a system Command without data
	 * 
	 * @param name
	 * @param cmdId
	 */
	public SystemCmd (String name, int cmdId) {
		super(name,cmdId);
	}

	/**
	 * Construct a system Command with data
	 * 
	 * @param name
	 * @param cmdId
	 * @param data
	 */
	public SystemCmd (String name, int cmdId, int data) {
		super(name,cmdId,data,0);
	}
	
	@Override
	public int getParentID() {
		return SYSTEM_ID;
	}

}
