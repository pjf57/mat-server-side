package com.pjf.mat.sim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;

import com.pjf.mat.api.Attribute;
import com.pjf.mat.api.Cmd;
import com.pjf.mat.api.Comms;
import com.pjf.mat.api.Element;
import com.pjf.mat.api.InputPort;
import com.pjf.mat.api.MatElementDefs;
import com.pjf.mat.api.Status;
import com.pjf.mat.sim.element.ElementFactory;
import com.pjf.mat.sim.lookup.LookupHandler;
import com.pjf.mat.sim.lookup.LookupRequest;
import com.pjf.mat.sim.model.ClockTick;
import com.pjf.mat.sim.model.LookupResult;
import com.pjf.mat.sim.model.LookupValidity;
import com.pjf.mat.sim.model.SimAccess;
import com.pjf.mat.sim.model.SimElement;
import com.pjf.mat.sim.model.SimHost;
import com.pjf.mat.sim.model.SimTime;
import com.pjf.mat.sim.types.ConfigItem;
import com.pjf.mat.sim.types.Event;
import com.pjf.mat.util.comms.BaseComms;
import com.pjf.mat.sim.router.Router;


public class MatSim extends BaseComms implements Comms, SimHost, SimAccess {
	private final static Logger logger = Logger.getLogger(MatSim.class);
	private final List<SimElement> simElements;
	private final Clock clk;
	private final Router router;
	private final LookupHandler lkuHandler;
	private boolean stopOnError;
	
	
	public MatSim() {
		stopOnError = true;
		simElements = new ArrayList<SimElement>();
		clk = new Clock(this);
		router = new Router(this);
		lkuHandler = new LookupHandler(this);
	}

	/**
	 * instantiate all the simulation elements
	 * 
	 * @param prjElements
	 * @throws Exception
	 */
	public void init(Collection<Element> prjElements) throws Exception {
		// instantiate the elements according to the project requirements
		for (Element el : prjElements) {
			if (el.getId() != 0) {
				SimElement se = ElementFactory.create(el.getId(),el.getHWType(),this);
				if (se != null) {
					simElements.add(se);
				} else {
					String msg = "cant create simulation element for type " + 
						el.getHWType() + " at el_id=" + el.getId();
						logger.error(msg);
						throw new Exception(msg);
				}
			}
		}
	}

	/**
	 * Transfer the configuration to the simulation elements
	 */
	@Override
	public void sendConfig(Collection<Element> collection) throws Exception {
		for (Element el : collection) {
			// set attributes
			for (Attribute attr : el.getAttributes()) {
				ConfigItem cfg = new ConfigItem(el.getId(),
						attr.getConfigId(),
						attr.getEncodedData());
				for (SimElement se : simElements) {
					se.putConfig(cfg);
				}
			}
			// set connections
			for (InputPort ip : el.getInputs()) {
				if (ip.getConnectedSrc() != null) {
					ConfigItem cfg = new ConfigItem(el.getId(),						
							MatElementDefs.EL_C_SRC_ROUTE,
							((ip.getId()-1) << 8) | ip.getConnectedSrc().getParent().getId());
					for (SimElement se : simElements) {
						se.putConfig(cfg);
					}
				}
			}
			// set config done for this element
			SimElement se = getSimElement(el.getId());
			if (se != null) {
				se.putConfig(new ConfigItem(el.getId(),MatElementDefs.EL_C_CFG_DONE,0));
			}
		}
	}

	@Override
	public Status requestStatus() throws Exception {
		for (SimElement se : simElements) {
			se.getStatus();
		}
		return null;
	}

	@Override
	public Status requestStatus(Element element) throws Exception {
		SimElement se = getSimElement(element.getId());
		if (se != null) {
			se.getStatus();
		}
		return null;
	}

	@Override
	public void sendCmd(Cmd cmd) throws Exception {
		for (SimElement se : simElements) {
			se.putCmd(cmd);
		}
	}
	
	/**
	 * Halt the simulation
	 * 
	 * @param reason
	 */
	public void haltSim(String reason) {
		logger.warn("Halting simulation because of " + reason);
		shutdown();
	}

	@Override
	public void shutdown() {
		logger.info("shutdown(): shutting down ...");
		for (SimElement se : simElements) {
			se.shutdown();
		}
		router.shutdown();
		lkuHandler.shutdown();
//		evtDistr.shutdown();
		clk.shutdown();
	}

	@Override
	public long getHWSignature() throws Exception {
		return mat.getSWSignature();
	}

	@Override
	public void publishEvent(Event evt, int latency) {
		logger.debug("PublishEvent(" + evt + "," + latency + ")");
		router.post(evt, latency);
	}

	@Override
	public void publishEventLog(int srcId, int intrumentId, int rawValue) {
		logger.debug("publishEventLog()");
		notifyEvent(srcId,intrumentId,rawValue);
	}

	@Override
	public void publishElementStatusUpdate(int elementId, String type,
			String basisState, int intState, int evtCount) {
		processNewStatusUpdate(elementId,type,basisState,intState,evtCount);		
	}

	/**
	 * Get the sim element for the particular ID
	 * 
	 * @param id - id of element
	 * @return SimElement or null
	 */
	protected SimElement getSimElement(int id) {
		if (id == 0) {
			return null;
		}
		return simElements.get(id-1);
	}

	@Override
	public void notifyError(String msg) {
		logger.error("notifyError()" + msg);
		if (stopOnError) {
			haltSim(msg);
		}
	}

	@Override
	public LookupResult lookup(int source, int instrumentId, int lookupKey)
			throws Exception {
		// TODO clean this up
//		String logstr = "lookup(src=" + source + ",instr=" + instrumentId +
//			",key=" + lookupKey + "): ";
		LookupResult result = null;
		result = lkuHandler.lookup(source, instrumentId, lookupKey);
//		for (SimElement se : simElements) {
//			result = se.handleLookup(instrumentId, lookupKey);
//			if (!result.getValidity().equals(LookupValidity.TIMEOUT)) {
//				break;
//			}
//		}
//		if (result == null) {
//			String msg = logstr + "Unexpected null value";
//			notifyError(msg);
//		} else {
//			logger.debug(logstr + " returned " + result);
//		}
		return result;
	}

	@Override
	public void postEventToElements(Event evt) throws ElementException {
		if (evt.getSrc() != 0) {
			for (SimElement se : simElements) {
				try {
					se.putEvent(evt);
				} catch (Exception e) {
					String msg = "Simulation error processing event into:" +
						se + " Event=" + evt + " - " + e.getMessage();
					notifyError(msg);
				}
			}
		}
	}

	@Override
	public SimTime getCurrentSimTime() {
		return clk.getSimTime();
	}

	@Override
	public LookupResult handleLookup(LookupRequest req) throws Exception {
		String logstr = "lookup(" + req + "): ";
	LookupResult result = null;
	for (SimElement se : simElements) {
		result = se.handleLookup(req.getInstrumentId(), req.getInstrumentId());
		if (!result.getValidity().equals(LookupValidity.TIMEOUT)) {
			break;
		}
	}
	if (result == null) {
		String msg = logstr + "Unexpected null value";
		notifyError(msg);
	} else {
		logger.debug(logstr + " returned " + result);
	}
	return result;
	}

	@Override
	public void publishMicroTick(SimTime simTime) {
		router.simMicroTick(simTime);
		lkuHandler.simMicroTick(simTime);
	}

	@Override
	public void publishClockTick(ClockTick tick) {
		for (SimElement se : simElements) {
			se.processTick(tick);
		}
	}

	/**
	 * Start the simulator
	 */
	public void start() {
		logger.info("-------------- sim start -------------");
		router.start();
		clk.start();
	}

}
