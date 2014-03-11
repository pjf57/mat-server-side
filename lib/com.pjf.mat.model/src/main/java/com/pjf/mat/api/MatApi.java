package com.pjf.mat.api;

import java.util.List;

import com.pjf.mat.api.util.SignatureResult;

import net.sf.json.JSONObject;

/** @model */
public interface MatApi {
	
	/**
	 * @return model of the design
	 */
	public MatModel getModel();
	
	/**
	 * Load a design into the model.
	 * 
	 * @param designText - The design encoded as JSON text
	 * @throws Exception if parse error
	 */
	public void loadDesign(String designText) throws Exception;
	
	/**
	 * Load a design into the model.
	 * 
	 * @param design - The design encoded as JSON Object
	 * @throws Exception if parse error
	 */
	public void loadDesign(JSONObject design) throws Exception;

	/**
	 * Send HW into configuration state if not already there
	 * @throws Exception if unable to get all elements into config state
	 * 
	 */
	void putIntoConfigMode() throws Exception;

	/**
	 * Configure the HW by encoding the model and sending it to the HW
	 * over the configured comms channel
	 * 
	 * @throws Exception
	 */
	public void configureHW() throws Exception;

	/**
	 * Send a command in realtime to a Cheetah Block in the hardware
	 * 
	 * @param cmd command specifying the Cheetah Block and the command to send
	 */
	public void sendCmd(Cmd cmd);

	/**
	 * Request status from the hardware
	 * will be returned asynchronously
	 */
	public void requestHWStatus();
	
	/**
	 * Reset the Cheetah Block counters
	 */
	public void resetCounters();

	/**
	 * Check the HW signature against the signature of the model
	 * This essentially confirms that the palette loaded into the model
	 * matches the HW build.
	 * 
	 * @throws Exception if no match or comms error
	 */
	public SignatureResult checkHWSignature() throws Exception;

	/**
	 * @return software signature
	 */
	public long getSWSignature();

	/**
	 * Synchronise the clock of the HW to the server clock
	 * Must be done before audit logs can be captured in the HW
	 * 
	 * @param originMs current timestamp in the server
	 * @throws Exception if communication error
	 */
	public void syncClock(long originMs) throws Exception;
	
	/**
	 * @return current time taking the syncClock into account
	 */
	public Timestamp getCurrentTime();

	/**
	 * Request the HW to send all remaining Lookup Bus Audit logs
	 * 
	 * @throws Exception if comms error
	 */
	public void reqLkuAuditLogs() throws Exception;

	/**
	 * Request the HW to send all remaining Router Audit logs
	 * 
	 * @throws Exception if comms error
	 */
	public void reqRtrAuditLogs() throws Exception;

	/**
	 * Recalculate all the calculated attributes in the model
	 * @throws Exception 
	 */
	public void recalcCalculatedAttrs() throws Exception;

	/**
	 * Recalculate all the attributes for an element
	 * 
	 * @param el	the element
	 * @return list of attributes that were recalculated
	 * @throws InstantiationException - if unable to load configured calc class
	 * @throws IllegalAccessException - if unable to load configured calc class
	 * @throws ClassNotFoundException - if unable to load configured calc class
	 * @throws Exception
	 */
	public List<Attribute> recalcElAttrs(Element el) throws InstantiationException,
			IllegalAccessException, ClassNotFoundException, Exception;


}
