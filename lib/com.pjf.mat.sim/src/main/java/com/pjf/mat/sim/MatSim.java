package com.pjf.mat.sim;

import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.pjf.mat.api.AttrSysType;
import com.pjf.mat.api.Attribute;
import com.pjf.mat.api.Cmd;
import com.pjf.mat.api.Comms;
import com.pjf.mat.api.Element;
import com.pjf.mat.api.InputPort;
import com.pjf.mat.api.LkuAuditLog;
import com.pjf.mat.api.MatElementDefs;
import com.pjf.mat.api.MatLogger;
import com.pjf.mat.api.RtrAuditLog;
import com.pjf.mat.api.Status;
import com.pjf.mat.api.Timestamp;
import com.pjf.mat.sim.element.ElementFactory;
import com.pjf.mat.sim.model.ClockTick;
import com.pjf.mat.sim.model.LookupResult;
import com.pjf.mat.sim.model.LookupValidity;
import com.pjf.mat.sim.model.SimAccess;
import com.pjf.mat.sim.model.SimElement;
import com.pjf.mat.sim.model.SimHost;
import com.pjf.mat.sim.model.TickdataResult;
import com.pjf.mat.sim.types.Event;
import com.pjf.mat.util.comms.UDPSktComms;
import com.pjf.mat.api.util.ConfigItem;
import com.pjf.mat.sim.router.Router;
import com.pjf.mat.api.LkuResult;


public class MatSim extends UDPSktComms implements Comms, SimHost, SimAccess {
	private final static Logger logger = Logger.getLogger(MatSim.class);
	private final Map<Integer,SimElement> simElements; // keyed on el id
	private final LkuAuditLogger lkuAuditLogger;
	private final RtrAuditLogger rtrAuditLogger;
	private Clock clk;
	private final Router router;
	private boolean stopOnError;
	private int nextTickref;
	
	public MatSim(MatLogger logger) throws SocketException, UnknownHostException {
		super("localhost");
		stopOnError = true;
		simElements = new HashMap<Integer,SimElement>();
		clk = new Clock(this,10,logger);
		router = new Router(this);
		lkuAuditLogger = new LkuAuditLogger();
		rtrAuditLogger = new RtrAuditLogger();
		nextTickref = 1;
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
					simElements.put(new Integer(el.getId()),se);
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
				if (attr.getConfigId() > 0) {
					// only config items that are not pseudo-attrs
					List<ConfigItem> configs = attr.getConfigList();
					for (ConfigItem cfg : configs) {
						for (SimElement se : simElements.values()) {
							se.putConfig(cfg);
						}
						lkuAuditLogger.putConfig(cfg);
						rtrAuditLogger.putConfig(cfg);
					}
				}
			}
			// set connections
			for (InputPort ip : el.getInputs()) {
				if (ip.getConnectedSrc() != null) {
					ConfigItem cfg = new ConfigItem(el.getId(), MatElementDefs.EL_C_SRC_ROUTE,
							ip.getConnectedSrc(),ip.getId());
					for (SimElement se : simElements.values()) {
						se.putConfig(cfg);
					}
				}
			}
			// set config done for this element
			SimElement se = getSimElement(el.getId());
			if (se != null) {
				se.putConfig(new ConfigItem(el.getId(),AttrSysType.SYSTEM,MatElementDefs.EL_C_CFG_DONE,0));
			}
		}
	}

	@Override
	public Status requestStatus() throws Exception {
		for (SimElement se : simElements.values()) {
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
		for (SimElement se : simElements.values()) {
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
//		for (SimElement se : simElements.values()) {
//			se.shutdown();
//		}
		router.shutdown();
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
	public void publishEventLog(Timestamp ts, int srcId, int srcPort, int intrumentId, 
			int tickref, int rawValue) {
		logger.debug("publishEventLog()");
		notifyEvent(ts,srcId,srcPort,intrumentId,tickref,rawValue);
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
		return simElements.get(new Integer(id));
	}

	@Override
	public void notifyError(String msg) {
		logger.error("notifyError()" + msg);
		if (stopOnError) {
			haltSim(msg);
		}
	}

	@Override
	public LookupResult lookup(int source, int instrumentId, int tickref, int lookupKey, int target)
			throws Exception {
		LookupResult result = null;
		Timestamp startTime = clk.getSimTime();
		String logstr = "lookup(src=" + source + ",instr=" + instrumentId +
				",key=" + lookupKey + "): ";
		Element responder = null;
		for (SimElement se : simElements.values()) {
			result = se.handleLookup(instrumentId, tickref, lookupKey, target);
			if (!result.getValidity().equals(LookupValidity.TIMEOUT)) {
				// this element handled the request - so break out of the loop
				responder = mat.getModel().getElement(se.getId());
				break;
			}
		}
		Timestamp endTime = clk.getSimTime();
		int lookupTime = (int) (endTime.getMicroticks() - startTime.getMicroticks());
		LkuResult resultCode = result.getLkuResult();
		Element sourceEl = mat.getModel().getElement(source);
		lkuAuditLogger.addLog(startTime,sourceEl,instrumentId,tickref,lookupKey,
				responder,resultCode,result.getFloatData(),lookupTime);
		logger.debug(logstr + " returned " + result);
		return result;
	}
	

	@Override
	public void postEventToElements(Event evt) throws ElementException {
		if (evt.getSrc() != 0) {
			Timestamp startDelivery = clk.getSimTime();
			Set<Element> takers = new HashSet<Element>();
			for (SimElement se : simElements.values()) {
				try {
					boolean taken = se.putEvent(evt);
					if (taken) {
						int id = se.getId();
						Element el = mat.getModel().getElement(id);
						if (el == null) {
							notifyError("postEventToElements(" + evt + ") taker id=" + id + " has no model element");
						} else {
							takers.add(el);
						}
					}
				} catch (Exception e) {
					String msg = "Simulation error processing event into:" +
						se + " Event=" + evt + " - " + e.getMessage();
					notifyError(msg);
				}
			}
			Timestamp endDelivery = clk.getSimTime();
			long qtime = startDelivery.getMicroticks() - evt.getTimestamp().getMicroticks();
			long deltime = endDelivery.getMicroticks() - startDelivery.getMicroticks();
			Element source = mat.getModel().getElement(evt.getSrc());
			rtrAuditLogger.addLog(evt.getTimestamp(),source,evt.getSrcPort(),takers,
					evt.getInstrument_id(),evt.getTickref(), (int) qtime, (int) deltime, 
					evt.getFloatData());
		}
	}

	@Override
	public Timestamp getCurrentSimTime() {
		return clk.getSimTime();
	}

	@Override
	public void publishMicroTick(Timestamp simTime) {
		router.simMicroTick(simTime);
		checkLkuAuditLogAutosend();
		checkRtrAuditLogAutosend();
	}

	private void checkLkuAuditLogAutosend() {
		Collection<LkuAuditLog> logs = lkuAuditLogger.checkAutoSend();
		if (logs != null) {
			notifyLkuAuditLogsReceipt(logs);			
		}
	}

	private void checkRtrAuditLogAutosend() {
		Collection<RtrAuditLog> logs = rtrAuditLogger.checkAutoSend();
		if (logs != null) {
			notifyRtrAuditLogsReceipt(logs);			
		}
	}

	@Override
	public void publishClockTick(ClockTick tick) {
		for (SimElement se : simElements.values()) {
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

	@Override
	public void synchroniseClock(int syncOrigin) throws Exception {
		logger.info("synchroniseClock(" + syncOrigin + ")");
		clk.sync(syncOrigin);		
	}

	@Override
	public void requestLkuAuditLogs() throws Exception {
		logger.info("requestLkuAuditLogs()");
		Collection<LkuAuditLog> logs = lkuAuditLogger.getLogs(80);
		notifyLkuAuditLogsReceipt(logs);
	}

	@Override
	public void requestRtrAuditLogs() throws Exception {
		logger.info("requestRtrAuditLogs()");
		Collection<RtrAuditLog> logs = rtrAuditLogger.getLogs(80);
		notifyRtrAuditLogsReceipt(logs);
	}

	@Override
	public int getTickref() {
		int ret = 0;
		synchronized(this) {
			ret = nextTickref;
			nextTickref++;
			if (nextTickref == 256) {
				nextTickref = 1;
			}
		}
		return ret;
	}

	@Override
	public TickdataResult tickdata(int source, int tickref, int tickdataKey) throws Exception {
		TickdataResult result = null;
		Timestamp startTime = clk.getSimTime();
		String logstr = "tickdata(src=" + source + ",tickref=" + tickref +
				",key=" + tickdataKey + "): ";
		Element responder = null;
		for (SimElement se : simElements.values()) {
			result = se.handleTickdata(tickref, tickdataKey);
			if (result.isValid()) {
				// this element handled the request - so break out of the loop
				responder = mat.getModel().getElement(se.getId());
				break;
			}
		}
		Timestamp endTime = clk.getSimTime();
		int lookupTime = (int) (endTime.getMicroticks() - startTime.getMicroticks());
		logger.debug(logstr + " returned " + result);
		return result;
	}
	

}
