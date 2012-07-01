package com.pjf.mat.api;

/** @model */
public interface InputPort {
	/** @model */
	public int getId();
	/** @model */
	public String getName();
	public void connectTo(OutputPort src);
	/** @model */
	public OutputPort getConnectedSrc();	// rtn null if not connected
	/** @model */
	public String getType();				// return data type of the port
}
