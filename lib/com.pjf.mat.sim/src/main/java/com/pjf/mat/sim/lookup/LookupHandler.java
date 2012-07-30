package com.pjf.mat.sim.lookup;


import com.pjf.mat.api.Timestamp;
import com.pjf.mat.sim.lookup.LookupRequest;
import com.pjf.mat.sim.model.LookupResult;
import com.pjf.mat.sim.model.SimAccess;

/**
 * This class manages the dispatch of lookup requests to lookup service
 * providers, one at a time, and blocks the threads of the others until
 * they get their turn.
 * 
 * It requires that simMicroTick() is called on each microTick.
 * 
 * @author pjf
 */
public class LookupHandler {
	private final LookupPump pump;
	
	public LookupHandler(SimAccess sim) {
		pump = new LookupPump(sim);
		pump.start();
	}
	
	/**
	 * Lookup a statistic for an instrument.
	 * This call blocks until the request has been serviced or a timeout occurs
	 * 
	 * @param source
	 * @param instrumentId
	 * @param lookupKey
	 * @return lookup result
	 */
	public LookupResult lookup(int source, int instrumentId, int lookupKey) {
		LookupRequest req = new LookupRequest(source,instrumentId,lookupKey);
		pump.push(req);
		LookupResult res = req.waitForResult();
		return res;		
	}

	/**
	 * Process regular ticks from the sim kernel
	 * 
	 * @param simTime current simulator time
	 */
	public void simMicroTick(Timestamp simTime) {
		pump.simMicroTick(simTime);
	}

	public void shutdown() {
		pump.shutdown();
	}


}
