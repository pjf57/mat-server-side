package com.pjf.mat.sim;

import com.pjf.mat.api.MatLogger;
import com.pjf.mat.sim.model.ClockTick;
import com.pjf.mat.sim.model.SimHost;

public class Clock extends Thread implements ClockTick{
	private MatLogger logger;
	private SimHost host;
	private int timestamp;
	private int period;
	private boolean shutdown;
	
	public Clock(SimHost host, int periodMs, MatLogger logger) {
		this.logger = logger;
		this.setName("Clock");
		this.host = host;
		timestamp = 0;
		this.period = periodMs;
		shutdown = false;
	}
	
	public void reset() {
		this.timestamp = 0;
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
