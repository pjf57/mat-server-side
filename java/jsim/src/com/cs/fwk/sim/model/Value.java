package com.cs.fwk.sim.model;

/**
 * Generic interface for modelling data with validity
 * 
 * @author pjf
 *
 */
public interface Value {
	public int getRawData();
	public boolean isValid();
}
