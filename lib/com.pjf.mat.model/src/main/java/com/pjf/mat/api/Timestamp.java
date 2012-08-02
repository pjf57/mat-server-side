package com.pjf.mat.api;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Model of simulation time
 */
public class Timestamp implements Comparable<Timestamp>{
	private final static long nsBase = 8;			// timebase in ns
	private long microticks;						// clks since origin
	
	/**
	 * Construct timestamp and set to invalid value
	 */
	public Timestamp() {
		microticks = -1;
	}
	
	/**
	 * Copy constructor for timestamp
	 * @param time
	 */
	public Timestamp(Timestamp time) {
		this.microticks = time.microticks;
	}

	/**
	 * Construct timestamp with specified microticks
	 * @param init
	 */
	public Timestamp(long init) {
		microticks = init;
	}

	public long getMicroticks() {
		return microticks;
	}
	
	@Override
	public int compareTo(Timestamp o) {
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
	public int hashCode() {
		return new Long(microticks).hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		if (! (o instanceof Timestamp)) {
			return false;
		}
		Timestamp os = (Timestamp) o;
		return this.microticks == os.microticks;
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
			str = ms.toPlainString() + "ms";
		} else {
			secs = secs.divide(new BigDecimal(1000000000L),9,RoundingMode.HALF_EVEN);
			return secs.toPlainString() + "s";
		}
		return str;
	}

	public boolean isValid() {
		return microticks != -1;
	}

}
