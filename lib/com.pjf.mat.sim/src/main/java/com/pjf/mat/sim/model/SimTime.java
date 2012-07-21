package com.pjf.mat.sim.model;

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

	@Override
	public String toString() {
		return "" + (microticks * nsBase) + "ns";
	}

	/**
	 * Add a number of microticks to the sim time
	 * 
	 * @param increment
	 */
	public void add(int increment) {
		microticks += increment;
	}
}
