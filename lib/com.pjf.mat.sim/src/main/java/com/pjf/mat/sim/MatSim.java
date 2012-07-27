package com.pjf.mat.sim;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.log4j.Logger;

import com.pjf.mat.api.Attribute;
import com.pjf.mat.api.Cmd;
import com.pjf.mat.api.Comms;
import com.pjf.mat.api.Element;
import com.pjf.mat.api.InputPort;
import com.pjf.mat.api.MatElementDefs;
import com.pjf.mat.api.MatLogger;
import com.pjf.mat.api.Status;
import com.pjf.mat.sim.element.ElementFactory;
import com.pjf.mat.sim.model.ClockTick;
import com.pjf.mat.sim.model.LookupResult;
import com.pjf.mat.sim.model.LookupValidity;
import com.pjf.mat.sim.model.SimElement;
import com.pjf.mat.sim.model.SimHost;
import com.pjf.mat.sim.types.ConfigItem;
import com.pjf.mat.sim.types.Event;
import com.pjf.mat.util.comms.BaseComms;


public class MatSim extends BaseComms implements Comms, SimHost {
	private final static Logger logger = Logger.getLogger(MatSim.class);
	private final List<SimElement> simElements;
	private Clock clk;
	private EventDistributor evtDistr;
	private boolean stopOnError;
	
	class EventDistributor extends Thread {
		private PriorityBlockingQueue<Event> eventQueue;
		private boolean shutdown;
		
		public EventDistributor() {
			setName("Event");
			eventQueue = new PriorityBlockingQueue<Event>();
			shutdown = false;
		}
		
		public void start() {
			logger.debug("Starting event distributor");
			super.start();
		}
		
		public void post(Event evt) {
			logger.debug("publishEvent(" + evt + ")");
			eventQueue.add(evt);
		}
		
		@Override
		public void run() {
			while (!shutdown) {
				try {
					Event evt = eventQueue.take();
					if (evt.getSrc() != 0) {
						for (SimElement se : simElements) {
							try {
								se.putEvent(evt);
							} catch (Exception e) {
								String msg = "Simulation error processing event into:" +
									se + " Event=" + evt + " - " + e.getMessage();
								logger.error(msg);
								notifyError(msg);
							}
						}
					}
				} catch (InterruptedException e) {
					// ignore interrupts
				}
			}
			logger.info("Shutdown.");
		}

		public void shutdown() {
			logger.debug("Shutting down ...");
			shutdown = true;
			// kick the queue
			Event evt = new Event(0,0,0);
			post(evt);
			}
		}
	
	public MatSim() {
		stopOnError = true;
		simElements = new ArrayList<SimElement>();
		evtDistr = new EventDistributor();
		evtDistr.start();
//		clk = clock;
//		clk.start();
	}

	public MatSim(MatLogger logger) {
		stopOnError = true;
		simElements = new ArrayList<SimElement>();
		evtDistr = new EventDistributor();
		evtDistr.start();
		clk = new Clock(this,10,logger);
		clk.start();
	}

	public void setClock(Clock clock) {
		this.clk = clock;
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
		evtDistr.shutdown();
		clk.shutdown();
	}

	@Override
	public long getHWSignature() throws Exception {
		return mat.getSWSignature();
	}

	@Override
	public void publishEvent(Event evt) {
		evtDistr.post(evt);
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

	@Override
	public void publishClockTick(ClockTick tick) {
		for (SimElement se : simElements) {
			se.processTick(tick);
		}
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
		String logstr = "lookup(src=" + source + ",instr=" + instrumentId +
			",key=" + lookupKey + "): ";
		LookupResult result = null;
		for (SimElement se : simElements) {
			result = se.handleLookup(instrumentId, lookupKey);
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

}
