package com.pjf.mat.sim.router;

import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

import com.pjf.mat.api.Timestamp;
import com.pjf.mat.sim.ElementException;
import com.pjf.mat.sim.model.SimAccess;
import com.pjf.mat.sim.model.SimElement;
import com.pjf.mat.sim.types.Event;


/**
 * This class accepts events to be delivered at a certain time, and
 * delivers them from a separate thread
 * 
 * @author pjf
 */
public class Router1 extends Thread {
	private final static Logger logger = Logger.getLogger(Router1.class);
	private final SimAccess sim;
	private PriorityBlockingQueue<Event> queue;
	private boolean shutdown;
	private Semaphore sem;
	private int nextTag;
	
	class queueOrderComparator implements Comparator<Event>{

		@Override
		public int compare(Event a, Event b) {
			return -1 * (a.compareTo(b));
		}
	}
	
	public Router1(SimAccess sim) {
		this.sim = sim;
		setName("Router1");
		nextTag = 0;
		shutdown = false;
		queue = new PriorityBlockingQueue<Event>(10,new queueOrderComparator());
		sem = new Semaphore(0);
	}

	public void start() {
		logger.debug("Starting Router");
		super.start();
	}
	
	public synchronized void post(Event evt, int latency) {
		logger.debug("publishEvent(" + evt + "," + latency + ")");
		if (latency <= 0) {
			// force latency of at least one, otherwise this will not get picked up
			latency = 1;
		}
		Timestamp evtTime = sim.getCurrentSimTime();
		evtTime.add(latency);
		if (logger.isDebugEnabled()) {
			logger.debug("post(" + evt + "," + latency + "): evtTime=" + evtTime +
					", queue: " + queue);
		}
		evt.setTag(nextTag);
		nextTag++;
		queue.add(evt);
	}
	
	@Override
	public void run() {
		while (!shutdown) {
			try {
				// wait for sim clock tick
				sem.acquire();
				synchronized(this) {
					Timestamp now = sim.getCurrentSimTime();
// FIXME					logger.warn("run() - take events from queue, time=" + now);
					while (true) {
						Event evt = queue.peek();
						if (evt == null) {
							break;
						}
						if (evt.getTimestamp().compareTo(now) > 0) {
							logger.warn("run() - qlen=" + queue.size() + 
									" future evt: " + evt);
							// this event is in the future
							break;
						}
						logger.warn("run() - qlen=" + queue.size() + 
								" peeked evt " + evt +
								" now=" + now);
						evt = queue.take();
						logger.debug("run() - got event: " + evt);
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
					}
				}
			} catch (InterruptedException e) {
				logger.warn("interrupt: " + e.getMessage());
				// ignore interrupts
			}
		}
		logger.info("Shutdown.");
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
		sem.release();
	}

	public void shutdown() {
		logger.debug("Shutting down ...");
		shutdown = true;
		// kick the queue
		Event evt = new Event(new Timestamp(),0,0,0);
		post(evt,0);
		}
	}