package com.pjf.mat.sim.router;

import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

import com.pjf.mat.sim.ElementException;
import com.pjf.mat.sim.model.SimAccess;
import com.pjf.mat.sim.model.SimElement;
import com.pjf.mat.sim.model.SimTime;
import com.pjf.mat.sim.router.SortedEventQueue;
import com.pjf.mat.sim.types.Event;

/**
 * This class accepts events to be delivered at a certain time, and
 * delivers them from a separate thread
 * 
 * @author pjf
 */
public class Router extends Thread {
	private final static Logger logger = Logger.getLogger(Router.class);
	private final SimAccess sim;
	private SortedEventQueue queue;
	private boolean shutdown;
	private SimTime simTime;
	private Semaphore sem;
	
	public Router(SimAccess sim) {
		this.sim = sim;
		setName("Router");
		shutdown = false;
		queue = new SortedEventQueue();
		simTime = sim.getCurrentSimTime();
		sem = new Semaphore(0);
	}

	public void start() {
		logger.debug("Starting event distributor");
		super.start();
	}
	
	public synchronized void post(Event evt, int latency) {
		logger.debug("publishEvent(" + evt + "," + latency + ")");
		if (latency <= 0) {
			// force latency of at least one, otherwise this will not get picked up
			latency = 1;
		}
		SimTime evtTime = simTime;
		evtTime.add(latency);
		if (logger.isDebugEnabled()) {
			logger.debug("post(" + evt + "," + latency + "): evtTime=" + evtTime +
					", queue: " + queue);
		}
		queue.add(evt,evtTime);
	}
	
	@Override
	public void run() {
		while (!shutdown) {
			try {
				// wait for sim clock tick
				sem.acquire();
				synchronized(this) {
					logger.debug("run() - take events from queue, time=" + simTime);
					// execute all events (if any) that should be executed at this time
					for (Event evt : queue.takeEvents(simTime)) {
						logger.debug("run() - got event: " + evt);
						if (evt.getSrc() != 0) {
							try {
								logger.debug("run() got evt " + evt);
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
	public void simMicroTick(SimTime simTime) {
		this.simTime = simTime;
		if (logger.isDebugEnabled()) {
			logger.debug("simMicroTick(" + simTime + "): queue is " + queue);
		}
		sem.release();
	}

	public void shutdown() {
		logger.debug("Shutting down ...");
		shutdown = true;
		// kick the queue
		Event evt = new Event(0,0,0);
		post(evt,0);
		}
	}