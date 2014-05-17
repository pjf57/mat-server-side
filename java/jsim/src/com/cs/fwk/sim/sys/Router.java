package com.cs.fwk.sim.sys;

import java.util.concurrent.PriorityBlockingQueue;

import org.apache.log4j.Logger;

import com.cs.fwk.sim.ElementException;
import com.cs.fwk.sim.model.BaseState;
import com.cs.fwk.sim.model.SimAccess;
import com.cs.fwk.sim.model.SimElement;
import com.cs.fwk.sim.types.Event;
import com.cs.fwk.api.Cmd;
import com.cs.fwk.api.MatElementDefs;
import com.cs.fwk.api.Timestamp;


/**
 * This class accepts events to be delivered at a certain time, and
 * delivers them from a separate thread
 * 
 * @author pjf
 */
public class Router {
	private final static Logger logger = Logger.getLogger(Router.class);
	private final SimAccess sim;
	private PriorityBlockingQueue<Event> queue;
	private int nextTag;
	private int evtCount;
	private BaseState baseState;
		
	public Router(SimAccess sim) {
		this.sim = sim;
		nextTag = 0;
		evtCount = 0;
		queue = new PriorityBlockingQueue<Event>();
		baseState = BaseState.CFG;
	}

	public void start() {
		logger.debug("Starting Router");
	}
	
	public synchronized void post(Event evt, int latency) {
		logger.debug("publishEvent(" + evt + "," + latency + ")");
		if (!baseState.equals(BaseState.RUN)) {
			logger.error("publishEvent(" + evt + "," + latency + ") - no publish as RTR in state: " + baseState);
		} else {
			evtCount++;
			if (latency <= 0) {
				// force latency of at least one, otherwise this will not get picked up
				latency = 1;
			}
			Timestamp evtTime = evt.getTimestamp();
			Timestamp newTime = sim.getCurrentSimTime();
			newTime.add(latency);
			evt.setTimestamp(newTime);
			if (logger.isDebugEnabled()) {
				logger.debug("post(" + evt + "," + latency + "): evtTime was " + evtTime +
						", queue: " + queue);
			}
			evt.setTag(nextTag);
			nextTag++;
			queue.add(evt);
		}
	}
	
	/**
	 * Take all events from queue upto and including specified time
	 * and propagate them into all the elements.
	 */
	private void propagateEvents() {
		Timestamp now = sim.getCurrentSimTime();
		logger.debug("propagateEvents() - take events from queue, time=" + now);
		while (true) {
			Event evt = queue.peek();
			if (evt == null) {
				break;
			}
			if (evt.getTimestamp().compareTo(now) > 0) {
				logger.debug("propagateEvents() - qlen=" + queue.size() + 
						" at time " + now + ", stopping at future evt: " + evt);
				// this event is in the future
				break;
			}
			logger.debug("propagateEvents() - qlen=" + queue.size() + 
					" peeked evt " + evt +
					" now=" + now);
			try {
				evt = queue.take();
				logger.debug("propagateEvents() - got event: " + evt);
				if (evt.getSrc() != 0) {
					try {
						sim.postEventToElements(evt);
					} catch (ElementException e) {
						SimElement se = e.getElement();
						String msg = "Simulation error processing event into:" +
							se + " Event=" + evt + " - " + e.getMessage();
						logger.error(msg);
						sim.notifyError(msg);
					}
				}
			} catch (InterruptedException e1) {
				logger.warn("propagateEvents() - interrupted: " + e1.getMessage());
			}
		}
	}

	/**
	 * Process regular ticks from the sim kernel
	 * 
	 * Each time it ticks, record the simTime locally and release the semaphore
	 * to see if there are any events to be delivered.
	 * 
	 * @param simTime current simulator time
	 */
	public void simMicroTick(Timestamp simTime) {
		if (logger.isDebugEnabled()) {
			logger.debug("simMicroTick(" + sim.getCurrentSimTime() + "): queue is " + queue);
		}
		propagateEvents();
	}

	public void shutdown() {
		logger.debug("Shutting down ...");
		}

	/**
	 * @return the router's base state
	 */
	public BaseState getBaseState() {
		return baseState;
	}
	
	public void putCmd(Cmd cmd) {
		if (cmd.getConfigId() == MatElementDefs.EL_C_RESET) {
			logger.info("putCmd() - reset to CFG state");
			baseState = BaseState.CFG;
		} else {
			logger.error("putCmd() - unhandled cmd: " + cmd);
		}
	}
	
	
	/**
	 * set RTR config done
	 */
	public void cfgDone() {
		logger.info("cfgDone()");
		baseState = BaseState.RUN;
	}

	/**
	 * @return the router's event count
	 */
	public int getCount() {
		return evtCount;
	}

	/**
	 * Reset the event counters
	 */
	public void resetCounters() {
		evtCount = 0;		
	}


}