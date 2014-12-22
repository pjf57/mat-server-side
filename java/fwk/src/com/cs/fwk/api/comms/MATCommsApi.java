package com.cs.fwk.api.comms;


import java.util.List;

import com.cs.fwk.api.Cmd;
import com.cs.fwk.api.Element;
import com.cs.fwk.api.MatApi;
import com.cs.fwk.api.NotificationCallback;
import com.cs.fwk.api.util.HwStatus;

/**
 * MAT-oriented Interface for communications to Cheetah Framework in target HW
 * 
 * @author pjf
 *
 */

public interface MATCommsApi extends LoopbackInt {
	
	/**
	 * Encode and transmit CB configuration from model to HW
	 * 
	 * @param CBs - list of CBs to send configuration for
	 * @throws Exception if error encoding or transmitting
	 */
	public void sendConfig(List<Element> CBs) throws Exception;
	
	/**
	 * Request status from the Cheetah Franework
	 * (Status is returned asynchronously via notification mechanism)
	 * 
	 * @throws Exception if it was not possible to deliver the request
	 * @see addNotificationSubscriber()
	 */
	public void requestStatus() throws Exception;
	
	/**
	 * Request status for a specific CB from the Cheetah Franework
	 * (Status is returned asynchronously via notification mechanism)
	 * 
	 * @throws Exception if it was not possible to deliver the request
	 * @see addNotificationSubscriber()
	 */
	public void requestStatus(Element cb) throws Exception;
	
	/**
	 * Request LKU audit logs to be transmitted from the Cheetah Franework
	 * (logs are returned asynchronously via notification mechanism)
	 * 
	 * @throws Exception if it was not possible to deliver the request
	 * @see addNotificationSubscriber()
	 */
	public void requestLkuAuditLogs() throws Exception;
	
	/**
	 * Request RTR audit logs to be transmitted from the Cheetah Franework
	 * (logs are returned asynchronously via notification mechanism)
	 * 
	 * @throws Exception if it was not possible to deliver the request
	 * @see addNotificationSubscriber()
	 */
	public void requestRtrAuditLogs() throws Exception;
	
	/**
	 * Send a command to the Hardware
	 * 
	 * @param cmd command to send
	 * @throws Exception
	 */
	public void sendCmd(Cmd cmd) throws Exception;
	
	/**
	 * Add a subscriber for receiving notifications for status change, 
	 * 
	 * @param subscriber
	 */
	public void addNotificationSubscriber(NotificationCallback subscriber);
	
	/**
	 * Send a request for the CF Status and wait until a reply is received 
	 * 
	 * @return HW signature
	 * @throws Exception if transmission error or timeout waiting for reply
	 */
	public long getHWSignature() throws Exception;
	
	/**
	 * Used to set the HW status artificially (eg: from a HW simulator)
	 * 
	 * @param st - new status to set
	 */
	public void setHwStatus(HwStatus st);
	
	/**
	 * @return last received HW status from CF
	 * Note: this does not send a request. Use getHWSignature to do that
	 */
	public HwStatus getHWStatus();
	
	/**
	 * Send a clock sync request to the HW
	 * 
	 * @param syncOriginMs Server clock timestamp (ms)
	 * @throws Exception if an error occured transmitting the request
	 */
	public void synchroniseClock(long syncOriginMs) throws Exception;
	
	/**
	 * Send a request to the HW to reset counters on all CBs.
	 * 
	 * @throws Exception if request could not be delivered
	 */
	public void resetCounters() throws Exception;

	/**
	 * Send a request to the HW to reset error state on all CBs.
	 * 
	 * @throws Exception if request could not be delivered
	 */
	public void resetErrorState() throws Exception;

	/**
	 * Send a request to the HW to reset the configuration for a particular (or all) CBs to default values.
	 * 
	 * @param cbId - ID of the CB (or MatElementDefs.EL_ID_ALL)
	 * @throws Exception if an error occured in delivering the request
	 */
	public void resetConfig(int cbId) throws Exception;
	
	/**
	 * Set the link for accessing the MAT and model
	 * 
	 * @param mat link to MAT API
	 */
	public void setMat(MatApi mat);
	
	/**
	 * @return the underlying connection used for transport to the HW
	 */
	public CxnInt getCxn();
	
	/**
	 * Shutdown the comms interface to the HW
	 */
	public void shutdown();

	/**
	 * @return version of API
	 */
	public String getApiVersion();

}
