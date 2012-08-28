package com.pjf.mat.test.util;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.pjf.mat.api.Timestamp;
import com.pjf.mat.sim.model.LookupResult;
import com.pjf.mat.sim.model.SimHost;
import com.pjf.mat.sim.types.Event;
import com.pjf.mat.util.Conversion;

import junit.framework.TestCase;

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
	public void publishEventLog(Timestamp ts, int srcId, int intrumentId,
			int rawValue) {
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
	public LookupResult lookup(int id, int instrumentId, int lookupKey)
			throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Timestamp getCurrentSimTime() {
		// TODO Auto-generated method stub
		return null;
	}

}
