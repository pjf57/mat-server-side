package com.pjf.mat.sim;

import org.apache.log4j.Logger;

import com.pjf.mat.sim.model.ClockTick;
import com.pjf.mat.sim.model.SimHost;

public class Clock extends Thread implements ClockTick{
	private final static Logger logger = Logger.getLogger(Clock.class);
	private SimHost host;
	private int timestamp;
	private int period;
	private boolean shutdown;
	
	public Clock(SimHost host, int periodMs) {
		this.setName("Clock");
		this.host = host;
		timestamp = 0;
		this.period = periodMs;
		shutdown = false;
	}
	
	@Override
	public void run() {
		while (!shutdown) {
			try {
				Thread.sleep(period);
			} catch (InterruptedException e) {
				// ignore
			}
			timestamp = (timestamp + 1) & 0xffff;
			logger.debug("Tick - " + timestamp);
			host.publishClockTick(this);  
		}
		logger.info("Shutdown.");
	}

	@Override
	public int getTimestamp() {
		return timestamp;
	}

	public void shutdown() {
		logger.debug("Shutting down ...");
		shutdown = true;
	}
}
