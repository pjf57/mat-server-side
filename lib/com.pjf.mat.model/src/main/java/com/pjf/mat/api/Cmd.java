package com.pjf.mat.api;

/** @model */
public interface Cmd {
	/** @model */
	public String getName();
	/** @model */
	public int getConfigId();
	/** @model opposite="cmds" */
	public Element getParent();		// get element that contains this cmd
	/** @model */
	public int getParentID();	// get ID of element that contains this cmd
	/** @model */
	public String getFullName();	// get full name including element ID, element Type
	/** @model */
	public int getArg();			// get arg associated with the cmd
	/** @model */
	public int getData();			// get data associated with the cmd
}
