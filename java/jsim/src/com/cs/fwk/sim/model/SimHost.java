package com.cs.fwk.sim.model;

import java.net.SocketException;
import java.net.UnknownHostException;

import com.cs.fwk.sim.types.Event;
import com.cs.fwk.api.ErrorState;
import com.cs.fwk.api.Timestamp;
import com.cs.fwk.api.comms.CxnInt;
import com.cs.fwk.api.comms.LoopbackInt;

/**
 * Interface for providing sim services to elements
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
	 * @param tickref		The tick reference for this event
	 * @param rawValue		The raw value of the event data
	 */
	public void publishEventLog(Timestamp ts, int srcId, int srcPort, int intrumentId, int tickref, int rawValue);

	/**
	 * publish a change in an element's status
	 * 
	 * @param elementId
	 * @param type
	 * @param basisState
	 * @param intState
	 * @param evtCount
	 * @param errState
	 */
	public void publishElementStatusUpdate(int elementId, String type,
			String basisState, int intState, int evtCount, ErrorState errState);

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
	 * @param arg			- arg to specify to service
	 * @param tickref		- tick reference to look up data for (may be zero)
	 * @param lookupKey		- type of data to find
	 * @param target		- element to target (or EL_ID_ALL)
	 * @param target2 
	 * @return				- lookup result including validity
	 * @throws Exception	- if an element had a problem replying
	 */
	public LookupResult lookup(int id, int instrumentId, int arg, int tickref, int lookupKey, int target) throws Exception;

	/**
	 * @return current simulation time in clks
	 */
	public Timestamp getCurrentSimTime();
	
	/**
	 * @return unique tickref value
	 * 
	 */
	public int getTickref();

	/**
	 * Lookup tickdata
	 * 
	 * @param elementId - id of requesting element
	 * @param tickref	- tick reference
	 * @param tickdataKey - selector
	 * @return - the result
	 * @throws Exception 
	 */
	public TickdataResult tickdata(int elementId, int tickref, int tickdataKey) throws Exception;

	/**
	 * Subscribe for incoming comms messages on a given port
	 * @param port	port to listen to
	 * @param cb	message handler
	 */
	public void subscribeIncomingMsgs(int port, LoopbackInt cb);
	
	/**
	 * get a UDP connection, either a loopback or normal
	 * 
	 * @param ip ("direct" for loopback)
	 * @return cxn
	 * @throws SocketException
	 * @throws UnknownHostException
	 */
	public CxnInt getCxnOrLoopback(String ip) throws SocketException, UnknownHostException;

}
