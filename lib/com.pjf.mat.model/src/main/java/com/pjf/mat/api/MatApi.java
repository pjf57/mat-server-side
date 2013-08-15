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
	 * Request status from the hardware
	 * 
	 * @return
	 */
	public Status getHWStatus();				// get status from HW
	
	/**
	 * Reset the Cheetah Block counters
	 */
	public void resetCounters();

	public void checkHWSignature() throws Exception;
	
	public long getSWSignature();
	
	public void syncClock(long originMs) throws Exception;
	
	/**
	 * @return current time taking the syncClock into account
	 */
	public Timestamp getCurrentTime();
	
	public void reqLkuAuditLogs() throws Exception;
	public void reqRtrAuditLogs() throws Exception;

}
