package com.cs.fwk.sim.test.util;

import java.net.SocketException;
import java.net.UnknownHostException;

import junit.framework.TestCase;


import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.cs.fwk.api.Timestamp;
import com.cs.fwk.api.comms.CxnInt;
import com.cs.fwk.api.comms.LoopbackInt;
import com.cs.fwk.sim.model.LookupResult;
import com.cs.fwk.sim.model.SimHost;
import com.cs.fwk.sim.model.TickdataResult;
import com.cs.fwk.sim.types.Event;
import com.cs.fwk.util.Conversion;


public class SimTestCase extends TestCase implements SimHost {
	private Logger logger = Logger.getLogger(SimTestCase.class);
	
	public SimTestCase() {
		BasicConfigurator.configure();
	}

	@Override
	public void publishEvent(Event evt, int latency) {
		logger.info("Got event: " + evt + " [" + Conversion.toHexIntString(evt.getRawData()) + "]");		
	}

	@Override
	public void publishEventLog(Timestamp ts, int srcId, int srcPort, int intrumentId,
			int tickref, int rawValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void publishElementStatusUpdate(int elementId, String type,
			String basisState, int intState, int evtCount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyError(String msg) {
		logger.error("Got error: " + msg);		
	}

	@Override
	public LookupResult lookup(int id, int instrumentId, int tickref, int lookupKey, int target)
			throws Exception {
		return lookupBehaviour(id,instrumentId,tickref,lookupKey);
	}
		
	protected LookupResult lookupBehaviour(int id, int instrumentId, int tickref, int lookupKey) throws Exception {
		throw new Exception ("Lookup used, but not implemented");
	}

	@Override
	public Timestamp getCurrentSimTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getTickref() {
		return 0;
	}

	@Override
	public TickdataResult tickdata(int elementId, int tickref, int tickdataKey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void subscribeIncomingMsgs(int port, LoopbackInt cb) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public CxnInt getCxnOrLoopback(String ip) throws SocketException,
			UnknownHostException {
		throw new UnknownHostException("Not supported");
	}
	
	/**
	 * @return default microtick period (ps)
	 */
	public int getMicroTickPeriod() {
		return 10000;
	}
	
	/**
	 * 	create a timestamp with the specified time and the default microtick period
	 * 
	 * @param time
	 * @return
	 */
	public Timestamp getTimestamp(long time) {
		return new Timestamp(time,getMicroTickPeriod());
	}

}
