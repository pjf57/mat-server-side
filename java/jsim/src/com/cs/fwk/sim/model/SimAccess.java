package com.cs.fwk.sim.model;

import com.cs.fwk.sim.ElementException;
import com.cs.fwk.sim.types.Event;
import com.cs.fwk.api.Timestamp;
import com.cs.fwk.api.comms.MATCommsApi;

/**
 * Interface to give access to sim services from other sim components
 * (not elements - they use SimHost).
 * 
 * @author pjf
 *
 */
public interface SimAccess {

	/**
	 * Post event to be injected into every element in the simulator
	 * 
	 * @param evt
	 */
	void postEventToElements(Event evt) throws ElementException;

	void notifyError(String msg);

	/**
	 * @return current simulation time in clks
	 */
	Timestamp getCurrentSimTime();

	/**
	 * Publish a microtick (ie: one HW clock cycle)
	 * 
	 * @param simTime
	 */

	void publishMicroTick(Timestamp simTime);
	/**
	 * publish a tick (ie: 10 microticks)
	 * 
	 * @param clock
	 */
	void publishClockTick(ClockTick tick);

	/**
	 * @return comms interface
	 */
	MATCommsApi getComms();


}
