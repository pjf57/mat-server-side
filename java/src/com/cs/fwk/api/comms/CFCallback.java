package com.cs.fwk.api.comms;

import java.util.List;

import com.cs.fwk.api.util.HwStatus;

/**
 * Callback interface for notification of incoming datagrams from the Cheetah Framework
 * 
 * @author pjf
 */

public interface CFCallback {

	/**
	 * process a Cheetah Framework status message received
	 * 
	 * @param st - the status received
	 */
	public void processCFStatus(HwStatus st);

	/**
	 * process a Cheetah Block status list received
	 * 
	 * @param st - list of raw Cheetah Block status reports
	 */
	public void processCBStatus(List<CBRawStatus> statusList);

	/**
	 * process a list of event logs received
	 * 
	 * @param logs - list of event logs
	 */
	public void processEvtLogs(List<EvtLogRaw> logs);

	/**
	 * process a list of LKU logs received
	 * 
	 * @param logs - list of LKU logs
	 */
	public void processLkuLogs(List<LkuAuditRawLog> logs);

	/**
	 * process a list of RTR logs received
	 * 
	 * @param logs - list of RTR logs
	 */
	public void processRtrLogs(List<RtrAuditRawLog> logs);

	/**
	 * process an unrecognised incoming message
	 * 
	 * @param destPort
	 * @param msg - the complete msg received
	 */
	public void processUnknownMsg(int destPort, byte[] msg);	

}
