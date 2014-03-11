package com.pjf.mat.sim;

import com.pjf.mat.api.Timestamp;
import com.pjf.mat.api.logging.MatLogger;
import com.pjf.mat.sim.model.ClockTick;
import com.pjf.mat.sim.model.SimAccess;

public class Clock extends Thread implements ClockTick{
	private MatLogger logger;
	private final int TICK_RATIO = 10;	// # microticks per tick
	private SimAccess host;
	private Timestamp simTime;
	private int timestamp;
	private int counter;
	private boolean shutdown;
	private long timestampOrigin;		// 32 bit origin of timestamp
	private boolean running;
	
	public Clock(SimAccess host, int periodMs, MatLogger logger) {
		this.logger = logger;
		this.setName("Clock");
		this.host = host;
		simTime = new Timestamp();
		timestamp = 0;
		counter = 0;
		shutdown = false;
		timestampOrigin = -1;
		running = false;
	}
	
	@Override
	public void start() {
		if (!running) {
			super.start();
			running = true;
		}
		shutdown = false;
	}
	
	public void reset() {
		this.timestamp = 0;
	}
	
	public long getOrigin() {
		return timestampOrigin;
	}
	
	/**
	 * returns a copy of the current time
	 * @return
	 */
	public Timestamp getSimTime() {
		return new Timestamp(simTime);
	}
	
	@Override
	public void run() {
		boolean didLog = false;
		while (running) {
			while (!shutdown) {
				didLog = false;
				processMicroTick();
			}
			if (!didLog) {
				logger.info("Shutdown.");
				didLog = true;
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// ignore
			}
		}
		logger.info("Clock exited.");
	}

	private void processMicroTick() {
		if (simTime.isValid()) {
			simTime.add(1);		// count another microtick in the sim time
			// FIXME should be trace
	//		logger.debug("processMicroTick(): " + simTime);
			host.publishMicroTick(getSimTime());
			counter++;
			if (counter >= TICK_RATIO) {
				counter = 0;
				timestamp = (timestamp + 1) & 0xffff;
				logger.debug("Tick - " + this);
				host.publishClockTick(this);  
			}
		}
	}

	@Override
	public int getTimestamp() {
		return timestamp;
	}

	/**
	 * Permanently kill the clock
	 */
	public void kill() {
		logger.debug("Killing the clock ...");
		shutdown = true;
		running = false;
	}

	/**
	 * Temp shutdown the clock. Can revive again with start()
	 */
	public void shutdown() {
		logger.debug("Shutting down ...");
		shutdown = true;
	}
	
	@Override
	public String toString() {
		return "simtime=" + simTime + ", timestamp=" + timestamp;
	}

	public void sync(long syncOrigin) {
		timestampOrigin = syncOrigin;
		simTime = new Timestamp(0,host.getComms().getHWStatus().getMicrotickPeriod());		
	}

}
