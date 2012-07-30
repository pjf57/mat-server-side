package com.pjf.mat.sim.model;

import com.pjf.mat.sim.ElementException;
import com.pjf.mat.sim.lookup.LookupRequest;
import com.pjf.mat.sim.types.Event;

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
	 * @return current simulation timestamps in clks
	 */
	SimTime getCurrentSimTime();

	/**
	 * Send lookup request to all elements and get response from one of them
	 * 
	 * @param req
	 * @return lookup result
	 * @throws Exception 
	 */
	LookupResult handleLookup(LookupRequest req) throws Exception;

	/**
	 * Publish a microtick (ie: one HW clock cycle)
	 * 
	 * @param simTime
	 */

	void publishMicroTick(SimTime simTime);
	/**
	 * publish a tick (ie: 10 microticks)
	 * 
	 * @param clock
	 */
	void publishClockTick(ClockTick tick);


}
