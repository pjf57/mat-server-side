package com.pjf.mat.sim.lookup;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

import com.pjf.mat.sim.model.LookupResult;
import com.pjf.mat.sim.model.SimAccess;
import com.pjf.mat.sim.model.SimTime;

/**
 * Maintains a queue of lookup requests and has a thread to
 * pump them through the lookup system
 * 
 * @author pjf
 */
public class LookupPump extends Thread {
	private final static Logger logger = Logger.getLogger(LookupPump.class);
	private final SimAccess sim;
	private final PriorityBlockingQueue<LookupRequest> queue;
	private final Semaphore sem;
	private SimTime simTime;
	private boolean shutdown;
	private boolean waiting;
	
	public LookupPump(SimAccess sim) {
		this.sim = sim;
		queue = new PriorityBlockingQueue<LookupRequest>();
		sem = new Semaphore(0);
		shutdown = false;
		waiting = false;
		setName("lookup pump");
		start();
	}

	/**
	 * Read from the queue and deliver each request sequentially, waiting
	 * for any specified timing.
	 */
	@Override
	public void run() {
		while (!shutdown) {
			try {
				LookupRequest req = queue.take();
				if (req != null) {
					LookupResult result;
					try {
						result = sim.handleLookup(req);
						if (result.getMicrotickDelay() != 0) {
							// wait until delay has passed
							logger.debug("run() - req " + req + " delaying for " +
									result.getMicrotickDelay() + " microticks.");
							synchronized(this) {
								waiting = true;
								sem.acquire(result.getMicrotickDelay());
								waiting = false;
							}
						}
						logger.debug("run() - req " + req + " providing result: " +
								result + " at simtime " + simTime);
						req.provideResult(result);
					} catch (Exception e) {
						String msg = "run() - exception looking up: " + e.getMessage();
						sim.notifyError(msg);
					}
				}
			} catch (InterruptedException e) {
				logger.warn("run() - Interrupted exception: " + e.getMessage());
			}
		}
	}
	
	public void push(LookupRequest req) {
		logger.debug("push(" + req + ")");
		queue.add(req);		
	}

	/**
	 * Process regular ticks from the sim kernel
	 * 
	 * Each time it ticks, record the simTime locally and release the semaphore
	 * to see if there are any events to be delivered.
	 * 
	 * @param simTime current simulator time
	 */
	public synchronized void simMicroTick(SimTime simTime) {
		this.simTime = simTime;
		if (waiting) {
			sem.release();
		}
	}

	public void shutdown() {
		logger.debug("Shutting down ...");
		shutdown = true;
		queue.add(null);		
	}

}
