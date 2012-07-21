package com.pjf.mat.sim.element;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Semaphore;
import org.apache.log4j.Logger;
import com.pjf.mat.api.MatElementDefs;
import com.pjf.mat.sim.bricks.BaseElement;
import com.pjf.mat.sim.bricks.InstrumentStore;
import com.pjf.mat.sim.model.BaseState;
import com.pjf.mat.sim.model.ClockTick;
import com.pjf.mat.sim.model.LookupResult;
import com.pjf.mat.sim.model.LookupValidity;
import com.pjf.mat.sim.model.SimElement;
import com.pjf.mat.sim.model.SimHost;
import com.pjf.mat.sim.types.ConfigItem;
import com.pjf.mat.sim.types.Event;
import com.pjf.mat.sim.types.FloatValue;

/**
 *--		processes price events on input and maintains
 *--		high, low, open, and close data for each instrument
 *--		for each of three periods: current, previous, and previous-1
 *--		ie: current, N, N-1 (where the current view changes as events come in)
 *--
 *--		provides this data by providing a lookup service
 *--		an extension point could be to add triggers to generate events
 *--		on certain criteria
 *
 * @author pjf
 *
 */
public class HLOC extends BaseElement implements SimElement {
	private final static Logger logger = Logger.getLogger(HLOC.class);
	private static final int LOOKUP_DLY = 2;	// lookup delay, microticks
	private static final int LATENCY = 0;	// input to output latency (microticks)
	private int c_period;			// num ticks in period (int)
	private int c_opMetric;			// metric to use for data in output events (lku req)
	private int c_throttle;			// min # (8ns) clks between outputs
	private int periodCnt;
	private Map<Period,MetricStore> store;	// store of metrics
	private OutputDriver opDrv;		// thread to emit output events
	private Set<Integer> iset;		// set of instruments that changed during the period

	enum Metric {HIGH,LOW,OPEN,CLOSE};
	enum Period {CURRENT,PREV,PRVM1};
	
	/**
	 * MetricStore stores all values for all metrics (in a given period)
	 * 
	 * @author pjf
	 */
	class MetricStore {
		private final String name;
		private Map<Metric,InstrumentStore> store;
		
		public MetricStore(String name) {
			this.name = name;
			store = new HashMap<Metric,InstrumentStore>();
			for (Metric m : Metric.values()) {
				store.put(m, new InstrumentStore(m + "/" + name));
			}
		}
		
		public InstrumentStore getStore(Metric m) {
			return store.get(m);
		}
		
		@Override
		public String toString() {
			return name + " metric store";
		}

		public void logState() {
			for (Metric m : Metric.values()) {
				store.get(m).logState();
			}
		}
	}

	/**
	 * OutputDriver generates output events from the PREV period
	 * 
	 * @author pjf
	 */
	class OutputDriver extends Thread {
		private Semaphore sem;
		private boolean shutdown;
		private Set<Integer> instrToOp;
		
		public OutputDriver() {
			setName("HLOC OP DRV");
			shutdown = false;
			sem = new Semaphore(0);
			start();
		}
		
		@Override
		public void run() {
			while (!shutdown) {
				try {
					sem.acquire();
					logger.debug("Start emitting output events for period");
					for (Integer instr : instrToOp) {
						if (shutdown) {
							break;
						}
						FloatValue val;
						try {
							val = getHLOCData(instr, c_opMetric);
							logger.debug(getIdStr() + "Emit instr=" + instr + "/" + val);
							if (val.isValid()) {
								Event evt = new Event(elementId,instr,val.getRawData());
								host.publishEvent(evt,LATENCY);
								// wait for throttle period
								// TODO - this is not really well timed cf hardware
								Thread.sleep(c_throttle);
							}
						} catch (Exception e) {
							String msg = getIdStr() + "Error outputting event: " + e.getMessage();
							host.notifyError(msg);
						}
						instr++;
					}
					logger.debug("Done emitting output events for period");
				} catch (InterruptedException e1) {
					logger.debug("run() - interrupted lock on sem");
				}
			}
			logger.info("Shutdown.");
		}
		
		public void emitEvents(Set<Integer> instrSet) {
			logger.debug("Emitting events ...");
			instrToOp = instrSet;
			sem.release();
		}

		public void shutdown() {
			shutdown = true;
			sem.release();
		}
	}
	
	public HLOC(int id, SimHost host) {
		super(id, MatElementDefs.EL_TYP_HLOC,host);
		store = new HashMap<Period,MetricStore>();
		store.put(Period.CURRENT,new MetricStore(" "));
		store.put(Period.PREV,new MetricStore(" "));
		store.put(Period.PRVM1,new MetricStore(" "));
		c_period = 0;
		c_opMetric = MatElementDefs.EL_HLOC_L_PREV_H;
		c_throttle = 0;
		periodCnt = 0;
		opDrv = new OutputDriver();
		iset = new HashSet<Integer>();
	}

	@Override
	protected void processConfig(ConfigItem cfg) {
		switch (cfg.getItemId()) {
		case MatElementDefs.EL_HLOC_C_PERIOD: 		c_period 	= cfg.getRawData();	break;
		case MatElementDefs.EL_HLOC_C_OP_THROT: 	c_throttle 	= cfg.getRawData();	break;
		case MatElementDefs.EL_HLOC_C_OP_METRIC:	c_opMetric  = cfg.getRawData(); break;
		default: logger.warn(getIdStr() + "Unexpected configuration: " + cfg); break;
		}
	}

	@Override
	protected void processEvent(int input, Event evt) throws Exception {
		int instr = evt.getInstrument_id();
		float val = evt.getFloatData();
		iset.add(new Integer(instr));
		synchronized(this){
			MetricStore st = store.get(Period.CURRENT);
			// high
			FloatValue currentHigh = st.getStore(Metric.HIGH).get(instr);
			if (currentHigh.isValid()) {
				if (val > currentHigh.getValue()) {
					logger.debug(getIdStr() + "New High=" + val);
					currentHigh.set(val);
				}
			} else {
				logger.debug(getIdStr() + "First High=" + val);
				st.getStore(Metric.HIGH).put(val, instr);
			}
			// low
			FloatValue currentLow = st.getStore(Metric.LOW).get(instr);
			if (currentLow.isValid()) {
				if (val < currentLow.getValue()) {
					logger.debug(getIdStr() + "New Low=" + val);
					currentLow.set(val);
				}
			} else {
				logger.debug(getIdStr() + "First Low=" + val);
				st.getStore(Metric.LOW).put(val, instr);
			}
			// Open
			FloatValue currentOpen = st.getStore(Metric.OPEN).get(instr);
			if (!currentOpen.isValid()) {
				logger.debug(getIdStr() + "Open=" + val);
				st.getStore(Metric.OPEN).put(val, instr);
			}
			// Close
			FloatValue currentClose = st.getStore(Metric.CLOSE).get(instr);
			if (currentClose.isValid()) {
				currentClose.set(val);
			} else {
				logger.debug(getIdStr() + "First Close=" + val);
				st.getStore(Metric.CLOSE).put(val, instr);
			}
		}
	}


	@Override
	public void processTick(ClockTick tick) {
		if (baseState == BaseState.RUN) {
			if (periodCnt >= c_period) {
				periodCnt = 0;
				// end of period -- switch stores and then emit events
				logger.info(getIdStr() + "End of period.");
				Set<Integer> instrSet;
				synchronized(this) {
					instrSet = new HashSet<Integer>(iset);
					iset.clear();
					store.put(Period.PRVM1, store.get(Period.PREV));
					store.put(Period.PREV, store.get(Period.CURRENT));
					store.put(Period.CURRENT,new MetricStore(" "));
				}
				logState();
				opDrv.emitEvents(instrSet);
			} else {
				periodCnt++;
			}
		}
	}
	
	@Override
	public LookupResult handleLookup(int instrumentId, int lookupKey) throws Exception {
		LookupResult result = new LookupResult(elementId,LookupValidity.TIMEOUT,LOOKUP_TIMEOUT_DLY);
		FloatValue data = getHLOCData(instrumentId,lookupKey);
		if (data != null) {
			result = new LookupResult(elementId,data,LOOKUP_DLY);
		}
		return result;
	}
	

	
	/**
	 * Get data from store based on lookup key
	 * 
	 * @param instrumentId
	 * @param lookupKey
	 * @return value or null (if lookup key did not translate to a store)
	 * @throws Exception if instrumentId out of range
	 */
	private FloatValue getHLOCData(int instrumentId, int lookupKey) throws Exception {
		FloatValue result = null;
		InstrumentStore st = null;
		switch (lookupKey) {
		case MatElementDefs.EL_HLOC_L_CURR_H: st = store.get(Period.CURRENT).getStore(Metric.HIGH); break;
		case MatElementDefs.EL_HLOC_L_CURR_L: st = store.get(Period.CURRENT).getStore(Metric.LOW); break;
		case MatElementDefs.EL_HLOC_L_CURR_O: st = store.get(Period.CURRENT).getStore(Metric.OPEN); break;
		case MatElementDefs.EL_HLOC_L_CURR_C: st = store.get(Period.CURRENT).getStore(Metric.CLOSE); break;

		case MatElementDefs.EL_HLOC_L_PREV_H: st = store.get(Period.PREV).getStore(Metric.HIGH); break;
		case MatElementDefs.EL_HLOC_L_PREV_L: st = store.get(Period.PREV).getStore(Metric.LOW); break;
		case MatElementDefs.EL_HLOC_L_PREV_O: st = store.get(Period.PREV).getStore(Metric.OPEN); break;
		case MatElementDefs.EL_HLOC_L_PREV_C: st = store.get(Period.PREV).getStore(Metric.CLOSE); break;

		case MatElementDefs.EL_HLOC_L_PRVM1_H: st = store.get(Period.PRVM1).getStore(Metric.HIGH); break;
		case MatElementDefs.EL_HLOC_L_PRVM1_L: st = store.get(Period.PRVM1).getStore(Metric.LOW); break;
		case MatElementDefs.EL_HLOC_L_PRVM1_O: st = store.get(Period.PRVM1).getStore(Metric.OPEN); break;
		case MatElementDefs.EL_HLOC_L_PRVM1_C: st = store.get(Period.PRVM1).getStore(Metric.CLOSE); break;
		}
		if (st != null) {
			result = st.get(instrumentId);
		}
		return result;
	}


	@Override
	public void shutdown() {
		logger.debug("Shutting down ...");
		opDrv.shutdown();
	}

	@Override
	protected String getTypeName() {
		return "HLOC";
	}
	
	/**
	 * Output the store state via the logger at debug level
	 */
	public void logState() {
		logger.debug(getIdStr() + "CURRENT:");	store.get(Period.CURRENT).logState();
		logger.debug(getIdStr() + "PREV:");		store.get(Period.PREV).logState();
		logger.debug(getIdStr() + "PREV-1:");	store.get(Period.PRVM1).logState();
	}

}
