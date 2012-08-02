package com.pjf.mat.api;

import javax.net.ssl.SSLEngineResult.Status;

/** @model */
public interface MatApi {	
	/**
	 * @model
	 */
	public MatModel getModel();					// access the model	
	/**
	 * @model
	 */
	public void configureHW() throws Exception;	// send config state of elements to HW
	/**
	 * @model
	 */
	public void sendCmd(Cmd cmd);				// send a cmd to the HW
	/**
	 * @model 
	 */
	public Status getHWStatus();				// get status from HW
	/**
	 * @model 
	 */
	public void checkHWSignature() throws Exception;
	
	public long getSWSignature();
	public void syncClock(int origin) throws Exception;

}
