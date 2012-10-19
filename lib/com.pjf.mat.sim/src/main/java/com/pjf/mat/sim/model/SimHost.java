package com.pjf.mat.sim.model;

import com.pjf.mat.api.Timestamp;
import com.pjf.mat.sim.types.Event;

/**
 * Interface for providing sim services to elemtns
 * 
 * @author pjf
 *
 */
public interface SimHost {
	/**
	 * Inject an event into the system
	 * 
	 * @param evt
	 * @param latency - # microticks model time from input to this event
	 */
	public void publishEvent(Event evt, int latency);
	
	/**
	 * push an event log into the system for logging
	 * 
	 * @param ts			the timestamp of the event
	 * @param srcId			The id of the element sending the event
	 * @param srcPort		The source output port number
	 * @param intrumentId	The ID of the instrument
	 * @param rawValue		The raw value of the event data
	 */
	public void publishEventLog(Timestamp ts, int srcId, int srcPort, int intrumentId, int rawValue);

	/**
	 * publish a change in an element's status
	 * 
	 * @param elementId
	 * @param type
	 * @param basisState
	 * @param intState
	 * @param evtCount
	 */
	public void publishElementStatusUpdate(int elementId, String type,
			String basisState, int intState, int evtCount);

	/**
	 * Indicate that an error has occurred
	 * 
	 * @param msg - message describing error
	 */
	public void notifyError(String msg);

	/**
	 * Lookup an instrument based value in the system
	 * (all elements will be polled to see if one of them can proved this data)
	 * 
	 * @param id 			- id of element doing the lookup
	 * @param instrumentId - instrument to look up data for
	 * @param lookupKey		- type of data to find
	 * @param target		- element to target (or EL_ID_ALL)
	 * @return				- lookup result including validity
	 * @throws Exception	- if an element had a problem replying
	 */
	public LookupResult lookup(int id, int instrumentId, int lookupKey, int target) throws Exception;

	/**
	 * @return current simulation time in clks
	 */
	Timestamp getCurrentSimTime();

}
