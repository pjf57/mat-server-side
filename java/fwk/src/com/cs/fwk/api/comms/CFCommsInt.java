package com.cs.fwk.api.comms;


/**
 * Low level interface to Cheetah Framework
 * 
 * @author pjf
 *
 */
public interface CFCommsInt {
	
	/**
	 * Set the datagram receipt callback
	 * The comms interface interprets and decodes the datagrams.
	 * 
	 * @param cb - object to call when datagrams are received from the Cheetah Framework
	 */
	public void setCallback(CFCallback cb);
	
	/**
	 * Reset and empty the config buffer.
	 * Use with addConfigItem(), addSysConfigItem(), addCmdItem(), sendConfig()
	 */
	public void resetConfigBuffer();

	/**
	 * Add a CB config item to the config buffer
	 * 
	 * @param cbId		- ID of CB to address
	 * @param configId	- ID of config item
	 * @param arg		- arg to send
	 * @param val		- data to send
	 */
	public void addConfigItem(int cbId, int configId, int arg, int val);

	/**
	 * Add a System config item to the config buffer
	 * 
	 * @param cbId		- ID of CB to address
	 * @param configId	- ID of config item
	 * @param arg		- arg to send
	 * @param val		- data to send
	 */
	public void addSysConfigItem(int cbId, int configId, int arg, int val);

	/**
	 * Add a Command item to the config buffer
	 * 
	 * @param cbId		- ID of CB to address
	 * @param cmdId		- ID of cmd
	 * @param arg		- arg to send
	 * @param val		- data to send
	 */
	public void addCmdItem(int cbId, int cmdId, int arg, int val);

	/**
	 * Add a connection to the config buffer
	 * 
	 * @param srcCBId	- ID of source CB
	 * @param srcOpPort	- output port number of source CB (0..n)
	 * @param dstCBId	- ID of dest CB
	 * @param dstIpPort - input port number of dest CB (0..n)
	 */
	public void addCxnItem(int srcCBId, int srcOpPort, int dstCBId, int dstIpPort);

	/**
	 * @return number of items that be loaded into the config buffer
	 */
	public int getConfigBufferSpace();

	/**
	 * @return number of items in the config buffer
	 */
	public int getConfigBufferItemCount();

	/**
	 * @return number of items in the config buffer
	 */
	public int getConfigBufferLength();

	/**
	 * Send the config to the Cheetah Framework
	 * 
	 * @throws Exception if it was not possible to deliver the config
	 * @see resetConfigBuffer
	 */
	public void sendConfig() throws Exception;
	

	/**
	 * Send a single command item to the Cheetah Framework
	 * (If you want to send multiple items, use the config buf)
	 * 
	 * @param cbId		- ID of CB to address
	 * @param cmdId		- ID of cmd
	 * @param arg		- arg to send
	 * @param val		- data to send
	 * 
	 * @throws Exception if it was not possible to deliver the config
	 */
	public void sendSingleCmd(int cbId, int cmdId, int arg, int val) throws Exception;

	/**
	 * Request status from the Cheetah Franework
	 * (Status is returned via a callback mechanism)
	 * 
	 * @throws Exception if it was not possible to deliver the request
	 */
	public void requestStatus() throws Exception;
	
	/**
	 * Request LKU Audit logs from the Cheetah Franework
	 * (logs are returned via a callback mechanism)
	 * 
	 * @throws Exception if it was not possible to deliver the request
	 */
	public void requestLkuAuditLogs() throws Exception;

	/**
	 * Request RTR Audit logs from the Cheetah Franework
	 * (logs are returned via a callback mechanism)
	 * 
	 * @throws Exception if it was not possible to deliver the request
	 */
	public void requestRtrAuditLogs() throws Exception;

	/**
	 * Request Status from the Cheetah Framework
	 * (status will be returned via callback)
	 * 
	 * @throws Exception if it was not possible to deliver the request
	 */
	public void requestCFStatus() throws Exception;

	/**
	 * Reset counters in the specified CB
	 * 
	 * @param cbId - ID of CB or ID_ALL
	 * 
	 * @throws Exception if it was not possible to deliver the request
	 */
	public void resetCounters(int cbId) throws Exception;
	
	
	/**
	 * Reset Error state in the specified CB
	 * 
	 * @param cbId - ID of CB or ID_ALL
	 * @throws Exception 
	 * 
	 * @throws Exception if it was not possible to deliver the request
	 */
	public void resetErrorState(int cbId) throws Exception;


	/**
	 * Reset config in the specified CB
	 * (The CB must be already in base state CFG)
	 * 
	 * @param cbId - ID of CB
	 * 
	 * @throws Exception if it was not possible to deliver the request
	 */
	public void resetCBConfig(int cbId) throws Exception;

	/**
	 * Synchronise the Cheetah Framwork clock with the server clock
	 * 
	 * @param serverClockMs - server clock origin (milliseconds)
	 * 
	 * @throws Exception if it was not possible to deliver the request
	 */
	public void synchroniseClock(long serverClockMs) throws Exception;

	/**
	 * Shutdown the communications interface
	 */
	public void shutdown();

	/**
	 * @return underlying connection being used
	 */
	public CxnInt getCxn();

	/**
	 * Method for injecting incoming messages as if they came from the underlying connection.
	 * 
	 * @param destPort
	 * @param msg
	 */
	public void handleIncomingMsg(int destPort, byte[] msg);

}
