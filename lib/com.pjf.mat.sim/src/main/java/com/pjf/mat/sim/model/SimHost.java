package com.pjf.mat.sim.model;

import com.pjf.mat.sim.types.Event;

public interface SimHost {
	/**
	 * Inject an event into the system
	 * 
	 * @param evt
	 */
	public void publishEvent(Event evt);
	
	/**
	 * push an event log into the system for logging
	 * 
	 * @param srcId			The id of the element sending the event
	 * @param intrumentId	The ID of the instrument
	 * @param rawValue		The raw value of the event data
	 */
	public void publishEventLog(int srcId, int intrumentId, int rawValue);

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
	 * Publish a clock tick
	 * @param tick
	 */
	public void publishClockTick(ClockTick tick);

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
	 * @param instrumentId - instrument to look up data for
	 * @param lookupKey		- type of data to find
	 * @return				- lookup result including validity
	 * @throws Exception	- if an element had a problem replying
	 */
	public LookupResult lookup(int instrumentId, int lookupKey) throws Exception;

}
