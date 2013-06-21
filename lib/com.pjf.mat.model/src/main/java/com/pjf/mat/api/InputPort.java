package com.pjf.mat.api;

/** @model */
public interface InputPort extends Port {
	/** @model */
	public String getName();
	public void connectTo(OutputPort src);
	/** @model */
	public OutputPort getConnectedSrc();	// rtn null if not connected
	public void removeCxn();
}
