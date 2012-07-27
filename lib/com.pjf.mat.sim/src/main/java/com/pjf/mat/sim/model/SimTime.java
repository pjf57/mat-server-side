package com.pjf.mat.sim.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Model of simulation time
 */
public class SimTime implements Comparable<SimTime>{
	private final static long nsBase = 8;			// timebase in ns
	private long microticks;						// clks since origin
	
	public long getMicroticks() {
		return microticks;
	}
	
	@Override
	public int compareTo(SimTime o) {
		if (microticks > o.microticks) {
			return 1;
		}
		if (microticks == o.microticks){
			return 0;
		}
		return -1;
	}

	/**
	 * Add a number of microticks to the sim time
	 * 
	 * @param increment
	 */
	public void add(int increment) {
		microticks += increment;
	}

	@Override
	public String toString() {
		String str;
		long ns = microticks * nsBase;
		BigDecimal secs = new BigDecimal(ns);
		if (ns < 1000000) {
			// less than 1ms - scale output in us
			BigDecimal us = secs.divide(new BigDecimal(1000L),3,RoundingMode.HALF_EVEN);
			str = us.toPlainString() + "us";
		} else if (ns < 1000000000) {		// less than 1s - scale output in ms
			BigDecimal ms = secs.divide(new BigDecimal(1000000L),6,RoundingMode.HALF_EVEN);
			str = ms.toPlainString() + "s";
		} else {
			secs = secs.divide(new BigDecimal(1000000000L),9,RoundingMode.HALF_EVEN);
			return secs.toPlainString() + "s";
		}
		return str;
	}

}
