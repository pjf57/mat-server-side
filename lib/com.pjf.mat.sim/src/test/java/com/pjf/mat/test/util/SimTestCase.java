package com.pjf.mat.test.util;

import junit.framework.TestCase;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.pjf.mat.api.InMsgCallbackInt;
import com.pjf.mat.api.Timestamp;
import com.pjf.mat.sim.model.LookupResult;
import com.pjf.mat.sim.model.SimHost;
import com.pjf.mat.sim.model.TickdataResult;
import com.pjf.mat.sim.types.Event;
import com.pjf.mat.util.Conversion;


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
	public void subscribeIncomingMsgs(int port, InMsgCallbackInt cb) {
		// TODO Auto-generated method stub
		
	}

}
