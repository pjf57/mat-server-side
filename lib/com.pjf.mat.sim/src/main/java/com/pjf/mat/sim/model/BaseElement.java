package com.pjf.mat.sim.model;

import org.apache.log4j.Logger;

import com.pjf.mat.api.Cmd;
import com.pjf.mat.api.MatElementDefs;
import com.pjf.mat.sim.model.SimHost;
import com.pjf.mat.sim.types.ConfigItem;
import com.pjf.mat.sim.types.Event;


public abstract class BaseElement implements SimElement {
	private final static Logger logger = Logger.getLogger(BaseElement.class);
	protected final int elementId;
	protected final int elementHWType;
	protected final SimHost host;
	private int[] srcRouting;
	private final int MAX_INPUTS = 4;
	protected BaseState baseState;
	protected int evtCount;
	
	public BaseElement(int id, int hwType, SimHost host) {
		this.elementId = id;
		this.elementHWType = hwType;
		this.host = host;
		baseState = BaseState.CFG;
		evtCount = 0;
		srcRouting = new int[MAX_INPUTS];
		for (int i=0; i<MAX_INPUTS; i++) {
			srcRouting[i] = 0;	// no connection
		}
	}

	/**
	 * Process event if it has been routed to one of our inputs
	 * @throws Exception 
	 */
	@Override
	public void putEvent(Event evt) throws Exception {
		for (int ip=0; ip<MAX_INPUTS; ip++) {
			if (evt.getSrc() == srcRouting[ip]) {
				logger.debug(getIdStr() + "Received Event on input " + (ip+1) +
						": " + evt);
				evtCount++;
				processEvent(ip+1, evt);
			}				
		}
	}

	@Override
	public void putConfig(ConfigItem cfg) {
		if (cfg.getElementId() == elementId) {
			// config item is for us - check if we can process generically
			
			switch (cfg.getItemId()) {
			case MatElementDefs.EL_C_SRC_ROUTE: 
				int input = (cfg.getRawData() / 256) & 3;	// 0..3
				int source = cfg.getRawData() & 0x3f;
				srcRouting[input] = source;
				break;
			case MatElementDefs.EL_C_RESET: 
				setBaseState(BaseState.RST);
				processReset();
				break;
			case MatElementDefs.EL_C_CFG_DONE: 
				setBaseState(BaseState.RUN);
				break;
			default:
				// pass config item up to the element
				processConfig(cfg);
			}
		}
	}

	protected void processReset() {
		// default behaviour is to move to the cfg phase
		setBaseState(BaseState.CFG);		
	}
	
	protected void setBaseState(BaseState newState) {
		logger.info(getIdStr() + "base state moved from " + baseState + " to " + newState);
		baseState = newState;
	}
	
	@Override
	public void getStatus() {
		host.publishElementStatusUpdate(elementId, getTypeName(),
				baseState.toString(), 0, evtCount);
	}

	@Override
	public void putCmd(Cmd cmd) {
		if (cmd.getParent().getId() == elementId) {
			processCmd(cmd);
		}		
	}

	@Override
	public void processTick(ClockTick tick) {
		// default behaviour is to ignore clock ticks
	}

	protected abstract void processConfig(ConfigItem cfg);
	
	protected void processCmd(Cmd cmd) {
		// default behaviour is to log a warning
		logger.warn(getIdStr() + "unexpected cmd:" + cmd);		
	}
	
	protected void processEvent(int input, Event evt) throws Exception {
		// default behaviour is to log a warning
		logger.warn(getIdStr() + "unexpected event:" + evt);		
	}

	protected String getIdStr() {
		return "SIM(id=" + elementId + 
				",type=" + elementHWType + "/" + getTypeName() + "):";
	}

	protected abstract String getTypeName();
	
	@Override
	public String toString() {
		return getIdStr() + "basisState=" + baseState + ", evtCount=" + evtCount;
	}
	
	@Override
	public void shutdown() {
		logger.debug("Element shutdown (default behaviour)");
	}
	
	@Override
	public LookupResult lookup(int instrumentId, int lookupKey) throws Exception {
		return new LookupResult(LookupValidity.TIMEOUT);
	}
}
