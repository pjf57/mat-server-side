package com.pjf.mat.sim.element;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.log4j.Logger;
import com.pjf.mat.api.MatElementDefs;
import com.pjf.mat.sim.model.BaseElement;
import com.pjf.mat.sim.model.BaseState;
import com.pjf.mat.sim.model.ClockTick;
import com.pjf.mat.sim.model.LookupResult;
import com.pjf.mat.sim.model.LookupValidity;
import com.pjf.mat.sim.model.SimElement;
import com.pjf.mat.sim.model.SimHost;
import com.pjf.mat.sim.types.ConfigItem;
import com.pjf.mat.sim.types.Event;
import com.pjf.mat.sim.types.FloatValue;
import com.pjf.mat.sim.types.InstrumentStore;

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
	private int c_period;			// num ticks in period (int)
	private Metric c_metric;		// metric to use for data in output events
	private int c_throttle;			// min # (8ns) clks between outputs
	private int periodCnt;
	private Map<Period,MetricStore> store;	// store of metrics
	private OutputDriver opDrv;		// thread to emit output events

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
	}

	/**
	 * OutputDriver generates output events from the PREV period
	 * 
	 * @author pjf
	 */
	class OutputDriver extends Thread {
		private Lock lock;
		private boolean shutdown;
		
		public OutputDriver() {
			setName("HLOC OP DRV");
			shutdown = false;
			lock = new ReentrantLock();
			lock.lock();	// take the lock, will be unlocked at end of period
			start();
		}
		
		@Override
		public void run() {
			while (!shutdown) {
				lock.lock();
				int instr = 0;
				InstrumentStore st = store.get(Period.PREV).getStore(c_metric);
				while ((instr < MatElementDefs.MAX_INSTRUMENTS) && !shutdown) {
					FloatValue val;
					try {
						val = st.get(instr);
						if (val.isValid()) {
							Event evt = new Event(elementId,instr,val.getRawData());
							host.publishEvent(evt);
							// wait for throttle period
							// TODO - this is not really well timed cf hardware
							Thread.sleep(c_throttle);
						}
					} catch (Exception e) {
						String msg = "HLOC(" + elementId + "): error outputting event: " + e.getMessage();
						logger.error(msg);
						host.notifyError(msg);
					}
					instr++;
				}
				logger.debug("Done emitting output events");
			}
			logger.info("Shutdown.");
		}
		
		public void emitEvents() {
			logger.debug("Emitting events ...");
			lock.unlock();
		}

		public void shutdown() {
			shutdown = true;
			lock.unlock();
		}
	}
	
	public HLOC(int id, SimHost host) {
		super(id, MatElementDefs.EL_TYP_HLOC,host);
		store = new HashMap<Period,MetricStore>();
		store.put(Period.CURRENT,new MetricStore("Current"));
		store.put(Period.PREV,new MetricStore("N"));
		store.put(Period.PRVM1,new MetricStore("N-1"));
		c_period = 0;
		c_metric = Metric.HIGH;
		c_throttle = 0;
		periodCnt = 0;
		opDrv = new OutputDriver();
	}

	@Override
	protected void processConfig(ConfigItem cfg) {
		switch (cfg.getItemId()) {
		case MatElementDefs.EL_HLOC_C_PERIOD: 		c_period 	= cfg.getRawData();	break;
		case MatElementDefs.EL_HLOC_C_OP_THROT: 	c_throttle 	= cfg.getRawData();	break;
		case MatElementDefs.EL_HLOC_C_OP_METRIC:
			switch(cfg.getRawData()) {
			case MatElementDefs.EL_HLOC_L_PREV_H: c_metric = Metric.HIGH;	break;
			case MatElementDefs.EL_HLOC_L_PREV_L: c_metric = Metric.LOW;	break;
			case MatElementDefs.EL_HLOC_L_PREV_O: c_metric = Metric.OPEN;	break;
			case MatElementDefs.EL_HLOC_L_PREV_C: c_metric = Metric.CLOSE;	break;
			default: logger.warn(getIdStr() + "Unexpected configuration metric: " + cfg); break;
			}
			break;
		default: logger.warn(getIdStr() + "Unexpected configuration: " + cfg); break;
		}
	}

	@Override
	protected void processEvent(int input, Event evt) throws Exception {
		int instr = evt.getInstrument_id();
		float val = evt.getFloatData();
		synchronized(store){
			MetricStore st = store.get(Period.CURRENT);
			// high
			FloatValue currentHigh = st.getStore(Metric.HIGH).get(instr);
			if (currentHigh.isValid()) {
				if (val > currentHigh.getValue()) {
					currentHigh.set(val);
				}
			} else {
				st.getStore(Metric.HIGH).put(val, instr);
			}
			// high
			FloatValue currentLow = st.getStore(Metric.LOW).get(instr);
			if (currentLow.isValid()) {
				if (val < currentLow.getValue()) {
					currentLow.set(val);
				}
			} else {
				st.getStore(Metric.LOW).put(val, instr);
			}
			// Open
			FloatValue currentOpen = st.getStore(Metric.OPEN).get(instr);
			if (!currentOpen.isValid()) {
				st.getStore(Metric.OPEN).put(val, instr);
			}
			// Close
			FloatValue currentClose = st.getStore(Metric.CLOSE).get(instr);
			if (currentClose.isValid()) {
				currentClose.set(val);
			} else {
				st.getStore(Metric.CLOSE).put(val, instr);
			}
		}
	}


	@Override
	public void processTick(ClockTick tick) {
		if (baseState == BaseState.RUN) {
			if (periodCnt >= c_period) {
				periodCnt = 0;
				synchronized(store) {
					store.put(Period.PRVM1, store.get(Period.PREV));
					store.put(Period.PREV, store.get(Period.CURRENT));
					store.put(Period.CURRENT,new MetricStore("Current"));
				}
			}	
		}		
	}
	
	@Override
	public LookupResult lookup(int instrumentId, int lookupKey) throws Exception {
		LookupResult result = new LookupResult(LookupValidity.TIMEOUT);
		InstrumentStore st = null;
		switch (lookupKey) {
		case MatElementDefs.EL_HLOC_L_CURR_H: st = store.get(Period.CURRENT).getStore(Metric.HIGH);
		case MatElementDefs.EL_HLOC_L_CURR_L: st = store.get(Period.CURRENT).getStore(Metric.LOW);
		case MatElementDefs.EL_HLOC_L_CURR_O: st = store.get(Period.CURRENT).getStore(Metric.OPEN);
		case MatElementDefs.EL_HLOC_L_CURR_C: st = store.get(Period.CURRENT).getStore(Metric.CLOSE);

		case MatElementDefs.EL_HLOC_L_PREV_H: st = store.get(Period.PREV).getStore(Metric.HIGH);
		case MatElementDefs.EL_HLOC_L_PREV_L: st = store.get(Period.PREV).getStore(Metric.LOW);
		case MatElementDefs.EL_HLOC_L_PREV_O: st = store.get(Period.PREV).getStore(Metric.OPEN);
		case MatElementDefs.EL_HLOC_L_PREV_C: st = store.get(Period.PREV).getStore(Metric.CLOSE);

		case MatElementDefs.EL_HLOC_L_PRVM1_H: st = store.get(Period.PRVM1).getStore(Metric.HIGH);
		case MatElementDefs.EL_HLOC_L_PRVM1_L: st = store.get(Period.PRVM1).getStore(Metric.LOW);
		case MatElementDefs.EL_HLOC_L_PRVM1_O: st = store.get(Period.PRVM1).getStore(Metric.OPEN);
		case MatElementDefs.EL_HLOC_L_PRVM1_C: st = store.get(Period.PRVM1).getStore(Metric.CLOSE);
		}
		if (st != null) {
			result = new LookupResult(st.get(instrumentId));
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

}
